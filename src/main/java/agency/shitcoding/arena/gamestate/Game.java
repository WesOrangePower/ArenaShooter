package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.events.MajorBuffTracker;
import agency.shitcoding.arena.models.*;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public abstract class Game {
    private final Set<Player> players = new HashSet<>();
    private final HashMap<Player, Integer> scores = new HashMap<>();
    private final MajorBuffTracker majorBuffTracker = new MajorBuffTracker();
    private final Set<Player> diedOnce = new HashSet<>();
    protected final Scoreboard scoreboard;
    protected RuleSet ruleSet;
    protected Arena arena;
    protected BukkitTask gameTimerTask;
    protected BukkitTask ammoActionBarTask;
    private GameStage gamestage = GameStage.WAITING;
    private List<LootPointInstance> lootPoints;
    private BukkitTask playerWaitingTimerTask;
    private Instant gameStart;
    private BossBar bossBar;
    private Objective scoreboardObjective;

    public Game(Arena arena, RuleSet ruleSet) {
        this.arena = arena;
        this.ruleSet = ruleSet;
        this.scoreboard = GameOrchestrator.getInstance().getScoreboard();
        arena.getLowerBound().getWorld().getNearbyEntities(BoundingBox.of(arena.getLowerBound(), arena.getUpperBound()))
                .stream().filter(e -> e.getType() == EntityType.DROPPED_ITEM)
                .map(e -> (Item) e)
                .peek(i -> i.getChunk().setForceLoaded(true))
                .peek(i -> i.getChunk().load())
                .forEach(Entity::remove);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        scores.remove(player);
        majorBuffTracker.getQuadDamageTeam().removePlayer(player);
        majorBuffTracker.getProtectionTeam().removePlayer(player);
        Optional.ofNullable(scoreboardObjective).ifPresent(o -> o.getScore(player).resetScore());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        if (bossBar != null) bossBar.removeViewer(Audience.audience(player));
        if (gamestage == GameStage.WAITING && players.isEmpty()) {
            endGame("Недостаточно игроков");
        } else if (gamestage == GameStage.IN_PROGRESS && players.size() < ruleSet.getMinPlayers()) {
            endGame("Недостаточно игроков");
        }
        players.forEach(p -> p.sendRichMessage("<red>" + player.getName() + " вышел из игры"));
        Lobby.getInstance().sendPlayer(player);
    }

    public void endGame(String reason) {
        this.gamestage = GameStage.FINISHED;
        playerWaitingTimerTask.cancel();
        if (gameTimerTask != null) gameTimerTask.cancel();
        if (ammoActionBarTask != null) ammoActionBarTask.cancel();
        if (bossBar != null) {
            StreamSupport.stream(bossBar.viewers().spliterator(), false)
                    .collect(Collectors.toUnmodifiableSet())
                    .forEach(viewer -> bossBar.removeViewer((Audience) viewer));
        }
        if (gamestage == GameStage.IN_PROGRESS || gamestage == GameStage.FINISHED) {
            Optional.ofNullable(getLootPoints())
                    .ifPresent(lp -> {
                        lp.forEach(i -> Optional.ofNullable(i.getSpawnTask()).ifPresent(BukkitTask::cancel));
                        removeLoot();
                        lp.clear();
                    });
        }

        Optional.ofNullable(scoreboardObjective).ifPresent(Objective::unregister);
        players.forEach(this::sengGameStats);

        players.forEach(p -> p.sendRichMessage("<green><bold>Игра закончилась: " + reason));
        players.forEach(Lobby.getInstance()::sendPlayer);
        GameOrchestrator.getInstance().removeGame(this);
    }

    private void sengGameStats(CommandSender sender) {
        sender.sendRichMessage("<green>-----Итоги----");
        scores.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .forEach(kv -> sender.sendRichMessage("<green><bold>" + kv.getKey().getName() + "<gold>: <red>" + kv.getValue()));
    }


    public void startGame() {
        playerWaitingTimerTask.cancel();
        for (Player player : players) {
            player.sendRichMessage("<green><bold>Игра началась");
            arena.spawn(player, this);
        }
        gamestage = GameStage.IN_PROGRESS;
        startGameStage2();
        this.lootPoints = new ArrayList<>(arena.getLootPoints().size());
        arena.getLootPoints().stream()
                .sorted(Comparator.comparingInt(LootPoint::getId))
                .forEach(this::createLootPointInstance);
        this.gameStart = Instant.now();
        this.gameTimerTask = Bukkit.getScheduler().runTaskTimer(
                ArenaShooter.getInstance(),
                this::onGameSecondElapsed,
                0L, 20L
        );
        this.ammoActionBarTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                ArenaShooter.getInstance(),
                () -> players.forEach(Ammo::displayAmmoActionBar),
                0L, 30L
        );

        scoreboardObjective = scoreboard.registerNewObjective(arena.getName(), Criteria.DUMMY,
                Component.text("Фраги", NamedTextColor.GOLD), RenderType.INTEGER);
        scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        players.stream()
                .peek(p -> p.setScoreboard(scoreboard))
                .forEach(p -> scoreboardObjective.getScore(p).setScore(0));
    }

    private void createLootPointInstance(LootPoint lootPoint) {
        LootPointInstance instance = new LootPointInstance(lootPoint);
        Bukkit.getScheduler().runTaskTimer(ArenaShooter.getInstance(),
                () -> {
                    Powerup powerup = instance.getLootPoint().getType();
                    if (powerup == Powerup.QUAD_DAMAGE && getMajorBuffTracker().getQuadDamageTicks() != null) {
                        return;
                    }
                    if (powerup == Powerup.PROTECTION && getMajorBuffTracker().getProtectionTicks() != null) {
                        return;
                    }
                    ItemStack itemStack = powerup.getItemStack();
                    Location location = instance.getLootPoint().getLocation().toCenterLocation().clone();

                    location.getNearbyEntities(1, 1, 1).stream()
                            .filter(e -> e instanceof Item)
                            .map(e -> (Item) e)
                            .forEach(Item::remove);

                    var item = location.getWorld().dropItem(location,
                            itemStack,
                            i -> {
                                i.getPersistentDataContainer().set(
                                        Keys.LOOT_POINT_KEY,
                                        PersistentDataType.INTEGER,
                                        instance.getLootPoint().getId()
                                );
                                i.setCanMobPickup(false);
                            }
                    );
                    item.setVelocity(new Vector(0f, .2f, 0f));

                    instance.setLooted(false);
                },
                lootPoint.getType().getOffset(),
                lootPoint.getType().getSpawnInterval()
        );

        this.lootPoints.add(instance.getLootPoint().getId(), instance);
    }

    protected void onGameSecondElapsed() {
        long remainingSeconds = ruleSet.getGameLenSeconds() - (Instant.now().getEpochSecond() - gameStart.getEpochSecond());
        float fraction = ((float) remainingSeconds) / ruleSet.getGameLenSeconds();
        fraction = Math.min(BossBar.MAX_PROGRESS, Math.max(BossBar.MIN_PROGRESS, fraction));
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        TextComponent title = Component.text("Осталось: " + timeString, TextColor.color(0xaa0000));
        boolean fractionIsOne = fraction < Vector.getEpsilon();

        if (fractionIsOne) {
            endGame("Время вышло");
        }

        if (bossBar == null) {
            this.bossBar = BossBar.bossBar(
                    title,
                    fraction,
                    BossBar.Color.RED,
                    BossBar.Overlay.PROGRESS
            );
            this.bossBar.addViewer(Audience.audience(players));
        }

        bossBar.progress(fraction);
        bossBar.name(title);
    }

    protected abstract void startGameStage2();

    public void addPlayer(Player player) {
        if (players.size() >= ruleSet.getMaxPlayers()) {
            player.sendRichMessage("<dark_red>Игра заполнена");
        }
        if (gamestage == GameStage.IN_PROGRESS) {
            bossBar.addViewer(Audience.audience(player));
            diedOnce.add(player);
            scoreboardObjective.getScore(player).setScore(0);
            player.setScoreboard(scoreboard);
        }
        players.add(player);
        scores.put(player, 0);
        player.sendRichMessage("<green>Вы присоединились к игре");
        players.forEach(p -> p.sendRichMessage("<green>" + player.getName() + " присоединился к игре"));
        getArena().spawn(player, this);
    }

    public void startAwaiting() {
        this.playerWaitingTimerTask = Bukkit.getScheduler().runTaskTimer(ArenaShooter.getInstance(),
                this::awaitingCycle,
                20,
                20 * 5
        );
    }

    private void awaitingCycle() {
        if (players.size() >= ruleSet.getMinPlayers()) {
            startGame();
            return;
        }
        players.forEach(p -> p.sendRichMessage(
                        String.format("<yellow>[%s/%s] <green>Ожидание игроков...", players.size(), ruleSet.getMinPlayers())
                )
        );
    }

    private void removeLoot() {
        getLootPoints()
                .stream()
                .map(lp -> lp.getLootPoint().getLocation())
                .forEach(l -> l.getNearbyEntities(3, 3, 3)
                        .stream()
                        .filter(e -> e instanceof Item)
                        .forEach(Entity::remove)
                );
    }

    public void onPlayerDeath(Player p) {
        diedOnce.add(p);
    }
}
