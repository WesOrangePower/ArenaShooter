package agency.shitcoding.arena.worlds;

import agency.shitcoding.arena.models.Arena;
import java.util.Optional;

public interface WorldManager {
  ArenaWorld newWorld(Arena arena);

  Optional<ArenaWorld> findByWorld(String worldName);

  void cleanUp();
}
