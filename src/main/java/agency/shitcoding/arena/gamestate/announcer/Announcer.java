package agency.shitcoding.arena.gamestate.announcer;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.localization.LangPlayer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Announcer implements AnnouncerQueue {
  private final ConcurrentMap<Player, ArrayDeque<Announcement>> queueMap = new ConcurrentHashMap<>();
  private final ConcurrentMap<Player, BukkitTask> taskMap = new ConcurrentHashMap<>();

  private static Announcer instance;

  public static Announcer getInstance() {
    if (instance == null) {
      instance = new Announcer();
    }
    return instance;
  }

  @Override
  public void announce(AnnouncerConstant constant, Player player) {
    var queue = queueMap.computeIfAbsent(player, (p) -> new ArrayDeque<>());
    queue.add(new Announcement(constant, player));

    startConsuming(player);
  }

  @Override
  public void announce(AnnouncerConstant constant, Collection<Player> player) {
    player.forEach(p -> announce(constant, p));
  }

  @Override
  public void clear() {
    queueMap.clear();
  }

  @Override
  public void clear(Player player) {
    queueMap.remove(player);
  }

  private void startConsuming(Player player) {
    if (taskMap.containsKey(player)) {
      return;
    }

    var task =
        Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> consume(player), 1);

    taskMap.put(player, task);
  }

  private void consume(Player player) {
    var queue = queueMap.get(player);

    if (queue == null || queue.isEmpty()) {
      taskMap.remove(player);
      return;
    }

    var announcement = queue.poll();
    announcement.announce();

    Bukkit.getScheduler()
        .runTaskLater(
            ArenaShooter.getInstance(),
            () -> consume(player),
            announcement
                .announcerConstant()
                .getSkip()
                .getSkipTime(LangPlayer.of(player).getLangContext().getSupportedLocale()));
  }
}
