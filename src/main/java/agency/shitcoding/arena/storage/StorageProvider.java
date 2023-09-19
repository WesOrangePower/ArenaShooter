package agency.shitcoding.arena.storage;

public class StorageProvider {
    private static ArenaStorage arenaStorage;


    private StorageProvider() {

    }

    public static ArenaStorage getArenaStorage() {
        if (arenaStorage == null) {
            arenaStorage = StorageFactory.createArenaStorage();
        }
        return arenaStorage;
    }
}
