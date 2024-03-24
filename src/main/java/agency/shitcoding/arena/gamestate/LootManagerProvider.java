package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.LootPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

public final class LootManagerProvider {

  private static final Map<String, LootManager> map = new HashMap<>();

  private LootManagerProvider() {
  }

  public static Optional<LootManager> get(Arena arena) {
    return Optional.ofNullable(map.get(arena.getName()));
  }

  public static void cleanup(Arena arena) {
    LootManager lootManager = map.get(arena.getName());
    if (lootManager == null) {
      return;
    }
    lootManager.cleanup();
    map.remove(arena.getName());
  }

  public static void create(Game game, Arena arena,
      UnaryOperator<Set<LootPoint>> lootPointPreprocessor) {
    LootManager lootManager = map.get(arena.getName());
    if (lootManager != null) {
      lootManager.cleanup();
    }

    Set<LootPoint> lootPoints = lootPointPreprocessor.apply(arena.getLootPoints());
    lootManager = new LootManager(lootPoints, game);
    map.put(arena.getName(), lootManager);
  }
}
