package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.gamestate.CosmeticsService;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CosmeticsUpdater {
  public static void refresh() {
    var oldWeaponMods = StorageProvider.getCosmeticsStorage().getAllWeaponMods();
    StorageProvider.getCosmeticsStorage().refresh();
    var newWeaponMods = StorageProvider.getCosmeticsStorage().getAllWeaponMods();
    if (oldWeaponMods.equals(newWeaponMods)) {
      return;
    }
    log.info("Changes in cosmetics file `{}` detected", ConfigurationCosmeticsStorage.FILE);
    logChanges(oldWeaponMods, newWeaponMods, "WeaponMod");

    CosmeticsService.getInstance().dropCache();
  }

  private static void logChanges(
      Map<String, List<String>> oldMap, Map<String, List<String>> newMap, String messagePrefix) {
    Maps.difference(oldMap, newMap)
        .entriesDiffering()
        .forEach(
            (key, value) ->
                log.info(
                    "{} changes for {}: {} -> {}",
                    messagePrefix,
                    key,
                    value.leftValue().size(),
                    value.rightValue().size()));
  }

  private CosmeticsUpdater() {}
}
