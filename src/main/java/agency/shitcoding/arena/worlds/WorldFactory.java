package agency.shitcoding.arena.worlds;

import agency.shitcoding.arena.models.Arena;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorldFactory implements WorldManager {
  private static WorldFactory instance;

  private final List<ArenaWorld> arenaWorlds = new ArrayList<>();

  private WorldFactory() {}

  public static WorldFactory getInstance() {
    if (instance == null) {
      instance = new WorldFactory();
    }
    return instance;
  }

  @Override
  public ArenaWorld newWorld(Arena arena) {
    var arenaWorld = new ArenaWorld(arena);

    arenaWorld.generate();
    arenaWorld.shiftArena();

    arenaWorlds.add(arenaWorld);

    return arenaWorld;
  }

  @Override
  public Optional<ArenaWorld> findByWorld(String worldName) {
    return arenaWorlds.stream()
        .filter(arenaWorld -> arenaWorld.getShifted().getUpperBound().getWorld().getName().equals(worldName))
        .findFirst();
  }

  @Override
  public void cleanUp() {
    arenaWorlds.forEach(ArenaWorld::destroy);
    arenaWorlds.clear();
  }
}
