package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.models.StoredHologram;
import agency.shitcoding.arena.storage.framework.ConfigurationMappable;
import agency.shitcoding.arena.storage.framework.ConfigurationMapper;
import agency.shitcoding.arena.storage.framework.annotation.MappedField;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.Configuration;

@Slf4j
public class MappedConfigurationHologramStorage implements HologramStorage {
  private final Configuration configuration;
  private final ConfigurationMapper<HologramStorageRoot> mapper;
  public static final String ROOT_PATH = "storage";
  private HologramStorageRoot root = null;

  public MappedConfigurationHologramStorage(Configuration configuration) {
    this.configuration = configuration;
    this.mapper = new ConfigurationMapper<>(HologramStorageRoot.class, configuration);
  }

  @Data
  @NoArgsConstructor
  public static class HologramStorageRoot implements ConfigurationMappable {
    private String id = "holograms";
    @MappedField
    private List<StoredHologram> holograms;
  }

  @Override
  public void reload() {
    var section = configuration.getConfigurationSection(ROOT_PATH);
    if (section == null) {
      configuration.createSection(ROOT_PATH);
    }
    root = mapper.read(ROOT_PATH);
  }


  @Override
  public Collection<StoredHologram> getHolograms() {
    reload();
    return root.getHolograms();
  }
}
