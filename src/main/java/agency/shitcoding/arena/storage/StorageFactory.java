package agency.shitcoding.arena.storage;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class StorageFactory {

  private static final Logger LOG = Logger.getLogger(StorageFactory.class.getName());


  public static ArenaStorage createArenaStorage() {
    return new ConfigurationArenaStorage(getConfiguration(ConfigurationArenaStorage.FILE_NAME));
  }

  public static FaqStorage createFaqStorage() {
    return new ConfigurationFaqStorage(getConfiguration(ConfigurationFaqStorage.FILE_NAME));
  }

  private static Configuration getConfiguration(String name) {
    try {
      File file = new File(name);
      if (file.createNewFile()) {
        LOG.info("Created new file " + name);
      }
      return YamlConfiguration.loadConfiguration(file);
    } catch (IOException e) {
      LOG.severe("Could not create file " + name);
      LOG.severe(e.getMessage());
      LOG.severe("Using memory configuration instead");
      return new MemoryConfiguration();
    }
  }
}
