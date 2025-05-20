package agency.shitcoding.arena.storage;

public final class StorageProvider {

  private static ArenaStorage arenaStorage;
  private static FaqStorage faqStorage;
  private static CosmeticsStorage cosmeticsStorage;
  private static HologramStorage hologramStorage;

  private StorageProvider() {}

  public static ArenaStorage getArenaStorage() {
    if (arenaStorage == null) {
      new MigrationDetector().detectMigration();
      arenaStorage = StorageFactory.createArenaStorage();
    }
    return arenaStorage;
  }

  public static FaqStorage getFaqStorage() {
    if (faqStorage == null) {
      faqStorage = StorageFactory.createFaqStorage();
    }
    return faqStorage;
  }

  public static CosmeticsStorage getCosmeticsStorage() {
    if (cosmeticsStorage == null) {
      cosmeticsStorage = StorageFactory.createCosmeticsStorage();
    }
    return cosmeticsStorage;
  }

  public static HologramStorage getHologramStorage() {
    if (hologramStorage == null) {
      hologramStorage = StorageFactory.createHologramStorage();
    }
    return hologramStorage;
  }

  public static void reload() {
    arenaStorage = null;
    faqStorage = null;
    cosmeticsStorage = null;
    hologramStorage = null;
  }
}
