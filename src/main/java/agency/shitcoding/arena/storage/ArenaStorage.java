package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.models.Arena;

import java.util.Collection;
import org.jspecify.annotations.Nullable;

public interface ArenaStorage {

  void storeArena(Arena arena);

  @Nullable Arena getArena(String name);

  Collection<Arena> getArenas();

  void deleteArena(Arena arena);
}
