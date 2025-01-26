package agency.shitcoding.arena.gamestate.announcer;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface SomeRunnable {
  void run(Player player);
}
