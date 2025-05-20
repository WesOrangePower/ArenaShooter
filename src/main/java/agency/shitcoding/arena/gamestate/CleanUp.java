package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.hologram.PersistentHologramManager;
import agency.shitcoding.arena.util.FileUtil;
import agency.shitcoding.arena.worlds.WorldFactory;
import lombok.extern.log4j.Log4j2;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

@Log4j2
public class CleanUp {

  public static void onShutdown() {
    WorldFactory.getInstance().cleanUp();
    log.info("Cleaning up holograms");
    PersistentHologramManager.getInstance().killAll();
  }

  public static void onStart() {
    log.info("Cleaning up holograms");
    removeOldHolograms();
    log.info("Attempting to clean up any existing worlds");
    removeGeneratedWorlds();
  }


  private static void removeGeneratedWorlds() {
    var root = ArenaShooter.getInstance().getDataFolder().toPath().toAbsolutePath()
        .getParent()
        .getParent();

    var files = root.toFile().listFiles((f, s) -> s.startsWith("generated__") && f.isDirectory());

    if (files == null) {
      return;
    }

    for (var file : files) {
      log.info("Deleting world: {}", file.getName());
      FileUtil.deleteWorld(file.getName());
    }

  }

  public static void removeOldHolograms() {
    Bukkit.getWorlds().stream()
        .flatMap(w -> w.getEntitiesByClasses(TextDisplay.class).stream())
//        .filter(e -> e.getPersistentDataContainer().has(Keys.getHologramKey(), PersistentDataType.BOOLEAN))
        .forEach(Entity::remove);
  }

  private CleanUp() {}
}
