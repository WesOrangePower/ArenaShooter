package agency.shitcoding.arena.worlds;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.models.*;
import agency.shitcoding.arena.models.door.Door;
import agency.shitcoding.arena.models.door.DoorTrigger;
import agency.shitcoding.arena.util.FileUtil;
import com.onarandombox.MultiverseCore.MultiverseCore;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class ArenaWorld {
  @Getter private final Arena origin;
  private @Nullable Arena shifted = null;
  @Getter private boolean generated;
  private @Nullable World world;

  public void generate() {
    if (generated) {
      return;
    }

    var worldName =
        String.format("generated__%s__%s", origin.getName(), System.currentTimeMillis());

    Optional<MultiverseCore> mvApi = ArenaShooter.getMultiverseApi();
    if (mvApi.isPresent()) {
      if (mvApi.get().getMVWorldManager().cloneWorld(origin.getName(), worldName)) {
        generated = true;
      }
      if (generated && mvApi.get().getMVWorldManager().loadWorld(worldName)) {
        this.world = ArenaShooter.getInstance().getServer().getWorld(worldName);
        return;
      }
    }

    if (!generated) {
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

      try {
        copyStructure(template.toFile(), dd.resolve(worldName).toFile());
      } catch (IOException e) {
        throw new RuntimeException(
            "Failed to copy template world for arena " + origin.getName(), e);
      }
    }

    this.world = ArenaShooter.getInstance().getServer().createWorld(getWorldCreator(worldName));
    this.generated = true;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void copyStructure(File template, File target) throws IOException {
    FileUtil.copyDirectoryRecursively(
        template,
        target,
        file -> !file.getName().equals("session.lock") && !file.getName().equals("uid.dat"));
    new File(target, "session.lock").delete();
    new File(target, "uid.dat").delete();
  }

  private WorldCreator getWorldCreator(String worldName) {
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
    FileUtil.deleteWorld(world);
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
            shifted.getWindTunnels().stream().map(WindTunnel::getFirstCorner).toList(),
            shifted.getWindTunnels().stream().map(WindTunnel::getSecondCorner).toList(),
            shifted.getDoorTriggers().stream().map(DoorTrigger::getLocation).toList(),
            shifted.getDoors().stream().map(Door::getEdge1).toList(),
            shifted.getDoors().stream().map(Door::getEdge2).toList(),
            shifted.getDoors().stream().map(Door::getDestinationCenter).toList(),
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
