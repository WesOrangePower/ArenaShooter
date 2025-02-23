package agency.shitcoding.arena.storage;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.file.YamlConfiguration;

import static agency.shitcoding.arena.storage.StorageFactory.ARENA_CFG_FILE;

@Slf4j
public class MigrationDetector {
  public void detectMigration() {
    if (!ARENA_CFG_FILE.exists()) {
      return;
    }

    var config = YamlConfiguration.loadConfiguration(ARENA_CFG_FILE);
    if (!config.contains("arena") || config.contains("storage")) {
      return;
    }
    log.info("Detected legacy storage format, migrating to new format");
    var migrator = new LegacyConfigurationArenaStorageToMappedConfigurationArenaStorageMigrator(
            ARENA_CFG_FILE,
        "arenas.old.yml"
        );
    migrator.migrate();
  }
}
