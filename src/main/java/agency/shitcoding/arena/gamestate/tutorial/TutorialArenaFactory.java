package agency.shitcoding.arena.gamestate.tutorial;

import agency.shitcoding.arena.models.*;
import agency.shitcoding.arena.models.door.Door;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class TutorialArenaFactory {

  public static @Nullable Arena getTutorialArena() {
    var world = Bukkit.getServer().getWorld("tutorial");
    if (world == null) {
      return null;
    }

    return new Arena(
        "tutorial",
        List.of("WesOrangePower"),
        new Location(world, -1, -61, -1).toBlockLocation(),
        new Location(world, 87, -40, 17).toBlockLocation(),
        Set.of(
            new LootPoint(
                "LP_1",
                new Location(world, 8.47, -59.00, 8.45, 3418.66f, -0.24f),
                true,
                Powerup.NOTHING,
                LootPointMarker.TUTORIAL_MARKER.getValue())),
        Set.of(
            new Portal(
                "PT_1",
                new Location(world, 23, -58, 16),
                new Location(world, 21, -56, 16),
                new Location(world, 36.46, -59.00, 8.67, 3420.20f, -2.07f))),
        Set.of(
            new WindTunnel(
                "WT_1",
                new Location(world, 2, -44, 9),
                new Location(world, 16, -42, 7),
                new Vector(1.2f, 0.2f, 0f)),
            new WindTunnel(
                "WT_2",
                new Location(world, 3, -46, 9),
                new Location(world, 5, -42, 7),
                new Vector(0f, 1f, 0f))),
        Set.of(),
        Set.of(
            new Door(
                "D_1",
                Door.ONE_TIME,
                10,
                -1,
                false,
                new Location(world, 23, -58, 15),
                new Location(world, 21, -56, 15),
                new Location(world, 22.5, -50, 15.5))),
        Set.of(),
        false,
        Set.of(),
        Set.of(RuleSet.TUTORIAL));
  }

  private TutorialArenaFactory() {}
}
