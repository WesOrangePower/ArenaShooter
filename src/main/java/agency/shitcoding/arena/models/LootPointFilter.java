package agency.shitcoding.arena.models;

@FunctionalInterface
public interface LootPointFilter {
  public static LootPointFilter all() {
    return lootPoint -> true;
  }

  boolean filter(LootPoint lootPoint);
}
