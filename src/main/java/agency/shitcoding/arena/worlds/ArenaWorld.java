package agency.shitcoding.arena.worlds;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.LootPoint;
import agency.shitcoding.arena.models.Portal;
import agency.shitcoding.arena.models.Ramp;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ArenaWorld {
  @Getter
  private final Arena origin;
  private Arena shifted = null;
  @Getter private boolean generated;
  private World world;

  public void generate() {
    if (generated) {
      return;
    }

    var dd =
        ArenaShooter.getInstance()
            .getDataFolder() // plugins/ArenaShooter
            .toPath()
            .toAbsolutePath()
            .getParent()
            .getParent();
    var template = dd.resolve(origin.getName());
    if (!Files.exists(template)) {
      throw new IllegalStateException(
          "Template world for arena " + origin.getName() + " does not exist");
    }

    var worldName =
        String.format("generated__%s__%s", origin.getName(), System.currentTimeMillis());
    try {
      copyStructure(template.toFile(), dd.resolve(worldName).toFile());
    } catch (IOException e) {
      throw new RuntimeException("Failed to copy template world for arena " + origin.getName(), e);
    }

    this.world = ArenaShooter.getInstance().getServer().createWorld(getWorldCreator(worldName));
    this.generated = true;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void copyStructure(File template, File target) throws IOException {
    FileUtils.copyDirectoryStructure(template, target);
    new File(target, "session.lock").delete();
    new File(target, "uid.dat").delete();
  }

  private @NotNull WorldCreator getWorldCreator(String worldName) {
    var world = origin.getUpperBound().getWorld();
    WorldCreator worldCreator = new WorldCreator(worldName);
    worldCreator.environment(world.getEnvironment());
    worldCreator.generator(world.getGenerator());
    worldCreator.generateStructures(false);
    worldCreator.seed(world.getSeed());
    worldCreator.keepSpawnLoaded(TriState.FALSE);
    return worldCreator;
  }

  public void destroy() {
    if (world == null) {
      return;
    }
    ArenaShooter.getInstance().getServer().unloadWorld(world, false);
    try {
      boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
      Runtime.getRuntime()
          .exec(
              isWindows
                  ? new String[] {"rmdir", "/s", "/q", world.getWorldFolder().getAbsolutePath()}
                  : new String[] {"rm", "-rf", world.getWorldFolder().getAbsolutePath()});
    } catch (IOException e) {
      throw new RuntimeException("Failed to delete world for arena " + origin.getName(), e);
    }
  }

  public void shiftArena() {
    this.shifted = origin.clone();
    shift(
        List.of(
            shifted.getRamps().stream().map(Ramp::getFirstLocation).toList(),
            shifted.getRamps().stream().map(Ramp::getSecondLocation).toList(),
            shifted.getPortals().stream().map(Portal::getFirstLocation).toList(),
            shifted.getPortals().stream().map(Portal::getSecondLocation).toList(),
            shifted.getPortals().stream().map(Portal::getTargetLocation).toList(),
            shifted.getLootPoints().stream().map(LootPoint::getLocation).toList()));

    shifted.setWeaponLootPoints(null);

    shift(shifted.getLowerBound(), shifted.getUpperBound());
  }

  public Arena getShifted() {
    if (!generated) {
      throw new IllegalStateException("World for arena " + origin.getName() + " is not generated");
    }
    if (shifted == null) shiftArena();
    return shifted;
  }

  public void shift(Location... collections) {
    if (!generated) {
      throw new IllegalStateException("World for arena " + origin.getName() + " is not generated");
    }

    for (var location : collections) {
      location.setWorld(world);
    }
  }

  public void shift(List<Collection<Location>> collections) {
    if (!generated) {
      throw new IllegalStateException("World for arena " + origin.getName() + " is not generated");
    }

    for (var collection : collections) {
      for (var location : collection) {
        location.setWorld(world);
      }
    }
  }
}
