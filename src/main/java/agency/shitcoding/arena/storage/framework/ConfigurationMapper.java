package agency.shitcoding.arena.storage.framework;

import org.bukkit.configuration.Configuration;

public class ConfigurationMapper<T extends ConfigurationMappable> {
  private final Class<T> type;
  private final ConfigurationMapperReader<T> reader;
  private final ConfigurationMapperWriter writer;

  public ConfigurationMapper(Class<T> type, Configuration bukkitConfiguration)
      throws ConfigurationMapperValidationException {
    this.type = type;
    this.writer = new ConfigurationMapperWriter(bukkitConfiguration);
    this.reader = new ConfigurationMapperReader<>(type, bukkitConfiguration);
    validate();
  }

  private void validate() throws ConfigurationMapperValidationException {
    var validation = new ConfigurationMapperTypeValidator(type).validateStructure();
    if (validation.isInvalid()) {
      throw validation.getError();
    }
  }

  public void write(String path, T object) {
    writer.write(path, object);
  }
  public void writeRoot(T object) {
    writer.write(null, object);
  }

  public T read(String path) {
    return reader.readMappable(path);
  }
}
