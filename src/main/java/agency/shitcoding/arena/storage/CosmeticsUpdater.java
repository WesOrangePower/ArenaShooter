package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.gamestate.CosmeticsService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CosmeticsUpdater {
  public static void refresh() {
    log.info("Refreshing cosmetics");
    StorageProvider.getCosmeticsStorage().refresh();
    CosmeticsService.getInstance().dropCache();
  }

  private CosmeticsUpdater() {}
}
