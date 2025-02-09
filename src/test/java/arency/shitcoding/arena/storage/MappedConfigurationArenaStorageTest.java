package arency.shitcoding.arena.storage;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.storage.LegacyConfigurationArenaStorage;
import agency.shitcoding.arena.storage.MappedConfigurationArenaStorage;
import be.seeseemelk.mockbukkit.MockBukkit;
import java.io.File;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MappedConfigurationArenaStorageTest {
  private final File arenaStorageFile = new File("arenas.yml");
  private final File oldArenaStorageFile = new File("arenas.yml.old");
  YamlConfiguration oldConfig = null;

  @Before
  public void setUp() throws Exception {
    var server = MockBukkit.mock();
    MockBukkit.load(ArenaShooter.class);
    server.addSimpleWorld("world");
    server.addSimpleWorld("q3dm7");
    server.addSimpleWorld("q3dm7_test");
    server.addSimpleWorld("q3ctf7");
    server.addSimpleWorld("q3dm6");
    var rs = getClass().getResource("/arenas.yml");
    assert rs != null;
    oldConfig = YamlConfiguration.loadConfiguration(new File(rs.toURI()));
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @After
  public void tearDown() {
    MockBukkit.unmock();
    arenaStorageFile.delete();
    oldArenaStorageFile.delete();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @SneakyThrows
  @Test
  public void testMigration() {
    arenaStorageFile.createNewFile();
    var newConfig = YamlConfiguration.loadConfiguration(arenaStorageFile);
    @SuppressWarnings("deprecation")
    var legacyConfigurationArenaStorage = new LegacyConfigurationArenaStorage(oldConfig);
    var mappedConfigurationArenaStorage = new MappedConfigurationArenaStorage(newConfig);
    legacyConfigurationArenaStorage
        .getArenas()
        .forEach(mappedConfigurationArenaStorage::storeArena);

    oldConfig.save(oldArenaStorageFile);
    newConfig.save(arenaStorageFile);

    assertTrue(arenaStorageFile.exists());
    assertTrue(newConfig.contains("storage"));
    assertTrue(newConfig.contains("storage.arenas"));
    assertTrue(newConfig.contains("storage.arenas.q3dm7"));
    assertTrue(newConfig.contains("storage.arenas.q3dm7.authors"));
    assertTrue(newConfig.getStringList("storage.arenas.q3dm7.authors").contains("mrx"));
  }
}
