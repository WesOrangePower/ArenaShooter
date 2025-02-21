package agency.shitcoding.arena.storage.framework;

public class ConfigurationMapperValidationException extends RuntimeException {
  public ConfigurationMapperValidationException(String message) {
    super(message);
  }

  public ConfigurationMapperValidationException(String message, Throwable e) {
    super(message, e);
  }
}
