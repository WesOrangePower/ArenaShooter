package agency.shitcoding.arena.storage;

public final class StorageProvider {

  private static ArenaStorage arenaStorage;
  private static FaqStorage faqStorage;


  private StorageProvider() {

  }

  public static ArenaStorage getArenaStorage() {
    if (arenaStorage == null) {
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
}
