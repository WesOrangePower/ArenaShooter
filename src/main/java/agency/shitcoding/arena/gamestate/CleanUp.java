package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.worlds.WorldFactory;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CleanUp {

  public static void onShutdown() {
    WorldFactory.getInstance().cleanUp();
  }

  public static void onStart() {
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
      boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
      String absolutePath = file.getAbsolutePath();
      try {
      Runtime.getRuntime()
          .exec(
              isWindows
                  ? new String[] {"rmdir", "/s", "/q", absolutePath}
                  : new String[] {"rm", "-rf", absolutePath});
      } catch (Exception e) {
        log.error("Failed to delete world: {}", file.getName(), e);
      }
    }

  }

  private CleanUp() {}
}
