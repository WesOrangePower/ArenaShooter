package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.ArenaShooter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class StorageFactory {

  private static final Logger LOG = Logger.getLogger(StorageFactory.class.getName());
  public static final File ARENA_CFG_FILE =
      new File(ArenaShooter.getInstance().getDataFolder(), "arenas.yml");

  public static ArenaStorage createArenaStorage() {
    return new MappedConfigurationArenaStorage(getConfiguration(ARENA_CFG_FILE));
  }

  public static FaqStorage createFaqStorage() {
    return new ConfigurationFaqStorage(getConfiguration(ConfigurationFaqStorage.FILE));
  }

  public static CosmeticsStorage createCosmeticsStorage() {
    return new ConfigurationCosmeticsStorage(getConfiguration(ConfigurationCosmeticsStorage.FILE));
  }

  private static Configuration getConfiguration(File file) {
    try {
      File parent = file.getParentFile();
      if (parent != null && !parent.exists() && parent.mkdirs()) {
        LOG.info(() -> "Created new directory " + parent);
      }
      if (file.createNewFile()) {
        LOG.info(() -> "Created new file " + file);
      }
      return YamlConfiguration.loadConfiguration(file);
    } catch (IOException e) {
      LOG.severe("Could not create file " + file);
      LOG.severe(e.getMessage());
      LOG.severe("Using memory configuration instead");
      return new MemoryConfiguration();
    }
  }

  private StorageFactory() {}
}
