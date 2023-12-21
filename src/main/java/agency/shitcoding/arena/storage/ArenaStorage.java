package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.models.Arena;

import java.util.Collection;

public interface ArenaStorage {
    void storeArena(Arena arena);

    Arena getArena(String name);

    Collection<Arena> getArenas();

    void deleteArena(Arena arena);
}
