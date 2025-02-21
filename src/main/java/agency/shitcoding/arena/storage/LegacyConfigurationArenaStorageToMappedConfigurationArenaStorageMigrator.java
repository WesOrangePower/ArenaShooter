package agency.shitcoding.arena.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LegacyConfigurationArenaStorageToMappedConfigurationArenaStorageMigrator {
  private final File legacyFile;
  private final String legacyFileCopyName;


  public void migrate() {
    log.info("Migrating legacy storage format to new format...");
    File legacyCopiedFile = new File(legacyFile.getParentFile(), legacyFileCopyName);
    if (!legacyFile.renameTo(legacyCopiedFile)) {
      throw new RuntimeException("Failed to rename legacy file");
    }
    log.info("Copied legacy config file to {}", legacyCopiedFile.getName());
    wipe(legacyFile);

    var legacyConfiguration = YamlConfiguration.loadConfiguration(legacyCopiedFile);
    @SuppressWarnings("deprecation")
    var legacyConfigurationArenaStorage = new LegacyConfigurationArenaStorage(legacyConfiguration);

    var newConfiguration = YamlConfiguration.loadConfiguration(legacyFile);
    var newConfigurationArenaStorage = new MappedConfigurationArenaStorage(newConfiguration);

    legacyConfigurationArenaStorage
        .getArenas()
        .forEach(newConfigurationArenaStorage::storeArena);

    log.info("Migration complete!");
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void wipe(File legacyFile) {
    try {
      legacyFile.delete();
      legacyFile.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
