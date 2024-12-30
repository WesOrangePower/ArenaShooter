package agency.shitcoding.arena.models;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface LootPointFilter {
  static LootPointFilter all() {
    return (lootPoint, player) -> true;
  }

  boolean filter(LootPoint lootPoint, Player player);
}
