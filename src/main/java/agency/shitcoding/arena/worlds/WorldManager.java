package agency.shitcoding.arena.worlds;

import agency.shitcoding.arena.models.Arena;

public interface WorldManager {
  ArenaWorld newWorld(Arena arena);

  void cleanUp();
}
