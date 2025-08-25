package agency.shitcoding.arena.storage;

import org.jspecify.annotations.Nullable;

public final class StorageProvider {

  private static @Nullable  ArenaStorage arenaStorage;
  private static @Nullable CosmeticsStorage cosmeticsStorage;

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
