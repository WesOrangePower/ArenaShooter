package arency.shitcoding.arena.storage;

import agency.shitcoding.arena.models.*;
import agency.shitcoding.arena.models.door.Door;
import agency.shitcoding.arena.models.door.DoorTrigger;
import agency.shitcoding.arena.storage.framework.ConfigurationMapper;
import java.io.File;
import java.util.List;
import java.util.Set;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArenaMapperTest {
  private File file;
  private YamlConfiguration configuration;
  private WorldMock world;

  @Before
  public void setUp() {
    file = new File("test.yml");
    ServerMock server = MockBukkit.mock();

    world = server.addSimpleWorld("world");
    configuration = new YamlConfiguration();
  }

  @After
  public void tearDown() {
    //noinspection ResultOfMethodCallIgnored
    file.delete();
    MockBukkit.unmock();
  }

  private Arena getArena() {
    return new Arena(
        "map",
        List.of("WesOrangePower", "markovav"),
        new Location(world, 1d, 2d, 3d),
        new Location(world, 1d, 3d, 3d),
        Set.of(
            new LootPoint("LP_1", new Location(world, 1d, 2d, 3d), true, Powerup.QUAD_DAMAGE, 0)),
        Set.of(
            new Portal(
                "P_1",
                new Location(world, 1d, 2d, 3d),
                new Location(world, 1d, 3d, 3d),
                new Location(world, 1d, 2d, 3d))),
        Set.of(
            new Ramp(
                "RP_1",
                new Location(world, 1d, 2d, 3d),
                new Location(world, 1d, 3d, 3d),
                true,
                new Vector(1, 2, 3))),
        Set.of(
            new Door(
                "DR_1",
                0,
                0,
                0,
                false,
                new Location(world, 1d, 2d, 3d),
                new Location(world, 1d, 3d, 3d),
                new Location(world, 1d, 3d, 3d))),
        Set.of(new DoorTrigger("DT_1", List.of("DR_1"), 0, new Location(world, 1d, 2d, 3d))),
        true,
        Set.of(),
        Set.of(RuleSet.DM, RuleSet.TDM));
  }

  @Test
  public void testCanWriteArena() {
    var arena = getArena();
    ConfigurationMapper<Arena> mapper = new ConfigurationMapper<>(Arena.class, configuration);

    mapper.write("arenas", arena);
    Arena readArena = mapper.read("arenas.map");
    assertNotNull(readArena);
    assertEquals(arena.getName(), readArena.getName());
    assertTrue(arena.getAuthors().containsAll(readArena.getAuthors()));
    assertEquals(arena.getLowerBound(), readArena.getLowerBound());
    assertEquals(arena.getUpperBound(), readArena.getUpperBound());
    assertTrue(arena.getLootPoints().containsAll(readArena.getLootPoints()));
    assertTrue(arena.getPortals().containsAll(readArena.getPortals()));
    assertTrue(arena.getRamps().containsAll(readArena.getRamps()));
    assertTrue(arena.getWeaponLootPoints().containsAll(readArena.getWeaponLootPoints()));
    assertTrue(arena.getDoors().containsAll(readArena.getDoors()));
    assertTrue(arena.getDoorTriggers().containsAll(readArena.getDoorTriggers()));
    assertEquals(arena.isAllowHost(), readArena.isAllowHost());
    assertTrue(arena.getTags().containsAll(readArena.getTags()));
    assertTrue(arena.getSupportedRuleSets().containsAll(readArena.getSupportedRuleSets()));
  }
}
