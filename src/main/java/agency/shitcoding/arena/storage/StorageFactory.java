package agency.shitcoding.arena.storage;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static agency.shitcoding.arena.storage.ConfigurationArenaStorage.FILE_NAME;

public class StorageFactory {

    private static final Logger LOG = Logger.getLogger(StorageFactory.class.getName());


    public static ArenaStorage createArenaStorage() {
        return new ConfigurationArenaStorage(getConfiguration());
    }

    private static Configuration getConfiguration() {
        try {
            File file = new File(FILE_NAME);
            if (file.createNewFile()) {
                LOG.info("Created new file " + FILE_NAME);
            }
            return YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            LOG.severe("Could not create file " + FILE_NAME);
            LOG.severe(e.getMessage());
            LOG.severe("Using memory configuration instead");
            return new MemoryConfiguration();
        }
    }
}
