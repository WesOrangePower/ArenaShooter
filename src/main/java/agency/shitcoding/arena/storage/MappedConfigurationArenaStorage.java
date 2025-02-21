package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.storage.framework.ConfigurationMappable;
import agency.shitcoding.arena.storage.framework.ConfigurationMapper;
import agency.shitcoding.arena.storage.framework.annotation.MappedField;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class MappedConfigurationArenaStorage implements ArenaStorage {
  private final Configuration configuration;
  private final ConfigurationMapper<ArenaStorageRoot> mapper;
  public static final String ROOT_PATH = "storage";
  private ArenaStorageRoot root = null;

  public MappedConfigurationArenaStorage(Configuration configuration) {
    this.configuration = configuration;
    this.mapper = new ConfigurationMapper<>(ArenaStorageRoot.class, configuration);
  }

  @Data
  @NoArgsConstructor
  public static class ArenaStorageRoot implements ConfigurationMappable {
    private String id = "arenas";
    @MappedField
    private List<Arena> arenas;
  }

  public void reload() {
    var section = configuration.getConfigurationSection(ROOT_PATH);
    if (section == null) {
      configuration.createSection(ROOT_PATH);
    }
    root = mapper.read(ROOT_PATH);
  }

  @Override
  public void storeArena(Arena arena) {
    read();
    root.getArenas().add(arena);
    writeRoot();
  }

  @Override
  public @Nullable Arena getArena(String name) {
    read();
    return root.getArenas().stream()
        .filter(arena -> arena.getName().equals(name))
        .findFirst()
        .orElse(null);
  }

  @Override
  public Collection<Arena> getArenas() {
    reload();
    return root.getArenas();
  }

  @Override
  public void deleteArena(Arena arena) {
    reload();
    root.getArenas().remove(arena);
    writeRoot();
  }

  private void writeRoot() {
    mapper.writeRoot(root);
    save();
  }

  private void save() {
    if (configuration instanceof YamlConfiguration yaml) {
      try {
        yaml.save(StorageFactory.ARENA_CFG_FILE);
      } catch (IOException e) {
        log.error("Failed to save configuration", e);
      }
    }
  }

  private void read() {
    if (root == null) {
      reload();
    }
  }
}
