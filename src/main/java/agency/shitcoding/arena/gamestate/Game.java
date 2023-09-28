package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.SoundConstants;
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
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public abstract class Game {
    protected final Scoreboard scoreboard;
    private final Set<Player> players = new HashSet<>();
    private final List<PlayerScore> scores = new ArrayList<>();
    private final MajorBuffTracker majorBuffTracker = new MajorBuffTracker();
    private final Set<Player> diedOnce = new HashSet<>();
    private final Consumer<String> announcer = s -> players.forEach(p -> p.playSound(p, s, SoundCategory.VOICE, .8f, 1f));
    protected RuleSet ruleSet;
    protected Arena arena;
    protected BukkitTask gameTimerTask;
    protected BukkitTask ammoActionBarTask;
    private GameStage gamestage = GameStage.WAITING;
    private Instant gameStart;
    private BossBar bossBar;
    private Objective scoreboardObjective;
    private PlayerWaitingManager waitingManager = null;

    protected Game(Arena arena, RuleSet ruleSet) {
        this.arena = arena;
        this.ruleSet = ruleSet;
        this.scoreboard = GameOrchestrator.getInstance().getScoreboard();
        arena.getLowerBound().getWorld().getNearbyEntities(BoundingBox.of(arena.getLowerBound(), arena.getUpperBound()))
                .stream().filter(e -> e.getType() == EntityType.DROPPED_ITEM)
                .map(e -> (Item) e)
                .forEach(item -> {
                    item.getChunk().setForceLoaded(true);
                    item.getChunk().load();
                    item.remove();
                });
    }

    public void removePlayer(Player player) {
        players.remove(player);
        scores.removeIf(p -> p.getPlayer().equals(player));
        majorBuffTracker.getQuadDamageTeam().removePlayer(player);
        majorBuffTracker.getProtectionTeam().removePlayer(player);
        Optional.ofNullable(scoreboardObjective).ifPresent(o -> o.getScore(player).resetScore());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        if (bossBar != null) bossBar.removeViewer(Audience.audience(player));
        boolean isEmptyWaiting = gamestage == GameStage.WAITING && players.isEmpty();
        boolean isTooFewPlayers = gamestage == GameStage.IN_PROGRESS && players.size() < ruleSet.getMinPlayers();
        if (isEmptyWaiting || isTooFewPlayers) {
            endGame("Недостаточно игроков");
        }
        players.forEach(p -> p.sendRichMessage("<red>" + player.getName() + " вышел из игры"));
        Lobby.getInstance().sendPlayer(player);
    }

    public void endGame(String reason) {
        this.gamestage = GameStage.FINISHED;
        waitingManager.cleanup();
        if (gameTimerTask != null) gameTimerTask.cancel();
        if (ammoActionBarTask != null) ammoActionBarTask.cancel();
        if (bossBar != null) {
            StreamSupport.stream(bossBar.viewers().spliterator(), false)
                    .collect(Collectors.toUnmodifiableSet())
                    .forEach(viewer -> bossBar.removeViewer((Audience) viewer));
        }
        LootManagerProvider.cleanup(arena);

        Optional.ofNullable(scoreboardObjective).ifPresent(Objective::unregister);
        if (ruleSet == RuleSet.DM || ruleSet == RuleSet.INSTAGIB) {
            players.forEach(this::sendGameStats);
        }

        players.forEach(p -> p.sendRichMessage("<green><bold>Игра закончилась: " + reason));
        players.forEach(Lobby.getInstance()::sendPlayer);
        GameOrchestrator.getInstance().removeGame(this);
    }

    private void sendGameStats(CommandSender sender) {
        sender.sendRichMessage("<green>-----Итоги----");
        scores.stream()
                .sorted()
                .forEach(score -> sender.sendRichMessage(
                        String.format("<green><bold>%s<gold>: <red>%d",
                                score.getPlayer().getName(),
                                score.getScore()
                        ))
                );
    }


    public void startGame() {
        waitingManager.cleanup();
        for (Player player : players) {
            player.sendRichMessage("<green><bold>Игра началась");
            announcer.accept(SoundConstants.FIGHT);
            arena.spawn(player, this);
        }
        gamestage = GameStage.IN_PROGRESS;
        LootManagerProvider.create(this, arena, this::preprocessLootPoints);
        startGameStage2();
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
                Component.text("Счёт", NamedTextColor.GOLD), RenderType.INTEGER);
        scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Player p : players) {
            p.setScoreboard(scoreboard);
            scoreboardObjective.getScore(p).setScore(0);
        }
    }

    protected abstract @NotNull Set<LootPoint> preprocessLootPoints(Set<LootPoint> lootPoints);

    protected void onGameSecondElapsed() {
        long remainingSeconds = ruleSet.getGameLenSeconds() - (Instant.now().getEpochSecond() - gameStart.getEpochSecond());
        float fraction = ((float) remainingSeconds) / ruleSet.getGameLenSeconds();
        fraction = Math.min(BossBar.MAX_PROGRESS, Math.max(BossBar.MIN_PROGRESS, fraction));
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        TextComponent title = Component.text("Осталось: " + timeString, TextColor.color(0xaa0000));
        boolean fractionIsOne = fraction < Vector.getEpsilon();

        timeEvents(remainingSeconds);

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

    private void timeEvents(long remainingSeconds) {
        boolean fiveMinutes = remainingSeconds == 300;
        if (fiveMinutes) {
            announcer.accept(SoundConstants.FIVE_MINUTE);
        }
        if (remainingSeconds == 60) {
            announcer.accept(SoundConstants.ONE_MINUTE);
        }
        if (remainingSeconds == 3) {
            announcer.accept(SoundConstants.THREE);
        }
        if (remainingSeconds == 2) {
            announcer.accept(SoundConstants.TWO);
        }
        if (remainingSeconds == 1) {
            announcer.accept(SoundConstants.ONE);
        }
    }

    protected abstract void startGameStage2();

    public void addPlayer(Player player) {
        if (players.size() >= ruleSet.getMaxPlayers()) {
            player.sendRichMessage("<dark_red>Игра заполнена");
        }
        if (waitingManager == null) {
            waitingManager = new PlayerWaitingManager(this);
            waitingManager.startAwaiting();
        }
        if (gamestage == GameStage.IN_PROGRESS) {
            bossBar.addViewer(Audience.audience(player));
            diedOnce.add(player);
            scoreboardObjective.getScore(player).setScore(0);
            player.setScoreboard(scoreboard);
        }
        players.add(player);
        scores.add(new PlayerScore(0, player));
        player.sendRichMessage("<green>Вы присоединились к игре");
        players.forEach(p -> p.sendRichMessage("<green>" + player.getName() + " присоединился к игре"));
        getArena().spawn(player, this);
    }

    public Optional<PlayerScore> getOptScore(Player p) {
        return scores.stream()
                .filter(sc -> sc.getPlayer().equals(p))
                .findFirst();
    }
    public @Nullable PlayerScore getScore(Player p) {
        PlayerScore pScore = null;
        for (PlayerScore score : scores) {
            if (score.getPlayer().equals(p)) {
                pScore = new PlayerScore(0, p);
                break;
            }
        }
        return pScore;
    }

    public void recalculateScore(Player p, int delta) {
        Collections.sort(scores);
        if (scores.isEmpty()) {
            scores.add(new PlayerScore(delta, p));
            return;
        }
        PlayerScore first = scores.get(0);
        PlayerScore pScore = getScore(p);
        pScore = pScore == null ? new PlayerScore(0, p) : pScore;

        // ==Sound zone
        Player firstPlayer = first.getPlayer();
        if (!firstPlayer.equals(p)) {
            // if tied
            if (pScore.getScore() + delta == first.getScore()) {
                p.playSound(p, SoundConstants.TIED_LEAD, SoundCategory.VOICE, .8f, 1f);
                firstPlayer.playSound(firstPlayer, SoundConstants.TIED_LEAD, SoundCategory.VOICE, .8f, 1f);
            }
            // if lead taken
            else if (pScore.getScore() + delta > first.getScore()) {
                p.playSound(p, SoundConstants.TAKEN_LEAD, SoundCategory.VOICE, .8f, 1f);
                firstPlayer.playSound(firstPlayer, SoundConstants.LOSTLEAD, SoundCategory.VOICE, .8f, 1f);
            }
            // if lead lost (-1)
            else if (pScore.getScore() + delta < first.getScore()) {
                p.playSound(p, SoundConstants.LOSTLEAD, SoundCategory.VOICE, .8f, 1f);
                firstPlayer.playSound(firstPlayer, SoundConstants.TAKEN_LEAD, SoundCategory.VOICE, .8f, 1f);
            }
        }
        // ==Sound zone end

        pScore.setScore(pScore.getScore() + delta);

        Collections.sort(scores);
    }

    public void onPlayerDeath(Player p) {
        diedOnce.add(p);
    }
}
