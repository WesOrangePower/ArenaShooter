package agency.shitcoding.arena.storage;

public final class StorageProvider {

  private static ArenaStorage arenaStorage;
  private static CosmeticsStorage cosmeticsStorage;

  private StorageProvider() {}

  public static ArenaStorage getArenaStorage() {
    if (arenaStorage == null) {
      new MigrationDetector().detectMigration();
      arenaStorage = StorageFactory.createArenaStorage();
    }
    return arenaStorage;
  }

  public static CosmeticsStorage getCosmeticsStorage() {
    if (cosmeticsStorage == null) {
      cosmeticsStorage = StorageFactory.createCosmeticsStorage();
    }
    return cosmeticsStorage;
  }
}
