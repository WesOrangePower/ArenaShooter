package agency.shitcoding.arena.worlds;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.LootPoint;
import agency.shitcoding.arena.models.Portal;
import agency.shitcoding.arena.models.Ramp;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

@RequiredArgsConstructor
public class ArenaWorld {
  private final Arena origin;
  @Getter
  private Arena arena;
  @Getter private boolean generated;
  private World world;

  public void generate() {
    if (generated) {
      return;
    }

    var dd = ArenaShooter.getInstance().getDataFolder().toPath();
    var template = dd.resolve(origin.getName());
    if (Files.exists(template)) {
      throw new IllegalStateException(
          "Template world for arena " + origin.getName() + " does not exist");
    }

    var worldName =
        String.format("generated__%s__%s", origin.getName(), System.currentTimeMillis());
    try {
      Files.copy(template, dd.resolve(worldName));
    } catch (IOException e) {
      throw new RuntimeException("Failed to copy template world for arena " + origin.getName(), e);
    }

    this.world = ArenaShooter.getInstance().getServer().createWorld(new WorldCreator(worldName));
    this.generated = true;
  }

  public void destroy() {
    if (world != null) {
      ArenaShooter.getInstance().getServer().unloadWorld(world, false);
      try {
        Files.delete(world.getWorldFolder().toPath());
      } catch (IOException e) {
        throw new RuntimeException("Failed to delete world for arena " + origin.getName(), e);
      }
    }
  }

  public void shiftArena() {
    this.arena = origin.clone();
    shift(
        List.of(
            arena.getRamps().stream().map(Ramp::getFirstLocation).toList(),
            arena.getRamps().stream().map(Ramp::getSecondLocation).toList(),
            arena.getPortals().stream().map(Portal::getFirstLocation).toList(),
            arena.getPortals().stream().map(Portal::getSecondLocation).toList(),
            arena.getPortals().stream().map(Portal::getTargetLocation).toList(),
            arena.getLootPoints().stream().map(LootPoint::getLocation).toList())
    );

    arena.setWeaponLootPoints(null);

    shift(arena.getLowerBound(), arena.getUpperBound());
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
