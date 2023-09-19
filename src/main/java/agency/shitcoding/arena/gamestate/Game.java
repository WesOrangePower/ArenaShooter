package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.events.MajorBuffTracker;
import agency.shitcoding.arena.models.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

@Getter
public abstract class Game {
    private final Set<Player> players = new HashSet<>();
    private final HashMap<Player, Integer> scores = new HashMap<>();
    private final boolean infiniteAmmo = false;
    private final MajorBuffTracker majorBuffTracker = new MajorBuffTracker();
    private GameStage gamestage = GameStage.WAITING;
    private List<LootPointInstance> lootPoints;
    protected RuleSet ruleSet;
    protected Arena arena;
    private BukkitTask bukkitTask;

    public Game(Arena arena, RuleSet ruleSet) {
        this.arena = arena;
        this.ruleSet = ruleSet;
    }

    public void removePlayer(Player player) {
        players.remove(player);
        scores.remove(player);
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
        bukkitTask.cancel();
        players.forEach(p -> p.sendRichMessage("<green><bold>Игра закончилась: " + reason));
        players.forEach(Lobby.getInstance()::sendPlayer);
        getLootPoints().forEach(i -> i.getSpawnTask().cancel()); // TODO: also kill all exisitng items
        getLootPoints().clear();
        GameOrchestrator.getInstance().removeGame(this);
    }

    public void startGame() {
        bukkitTask.cancel();
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
    }

    private void createLootPointInstance(LootPoint lootPoint) {
        LootPointInstance instance = new LootPointInstance(lootPoint);
        Bukkit.getScheduler().runTaskTimer(ArenaShooter.getInstance(),
                () -> {
                    ItemStack itemStack = instance.getLootPoint().getType().getItemStack();
                    Location location = instance.getLootPoint().getLocation().toCenterLocation().clone().add(0, 0, 0);

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

    protected abstract void startGameStage2();

    public void addPlayer(Player player) {
        if (players.size() >= ruleSet.getMaxPlayers()) {
            player.sendRichMessage("<dark_red>Игра заполнена");
        }
        players.add(player);
        scores.put(player, 0);
        player.sendRichMessage("<green>Вы присоединились к игре");
        players.forEach(p -> p.sendRichMessage("<green>" + player.getName() + " присоединился к игре"));
        getArena().spawn(player, this);
    }

    public void startAwaiting() {
        this.bukkitTask = Bukkit.getScheduler().runTaskTimer(ArenaShooter.getInstance(),
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

}
