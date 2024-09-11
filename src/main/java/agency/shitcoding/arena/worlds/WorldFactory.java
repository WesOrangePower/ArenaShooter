package agency.shitcoding.arena.worlds;

import agency.shitcoding.arena.models.Arena;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class WorldFactory implements WorldManager {

  @Getter private static WorldFactory instance;

  private final List<ArenaWorld> arenaWorlds = new ArrayList<>();

  private WorldFactory() {}

  @Override
  public ArenaWorld newWorld(Arena arena) {
    var arenaWorld = new ArenaWorld(arena);

    arenaWorld.generate();

    arenaWorlds.add(arenaWorld);

    return arenaWorld;
  }

  @Override
  public void cleanUp() {
    arenaWorlds.forEach(ArenaWorld::destroy);
    arenaWorlds.clear();
  }
}
