package arency.shitcoding.arena.storage;

import static org.junit.Assert.assertTrue;

import agency.shitcoding.arena.storage.framework.ConfigurationMappable;
import agency.shitcoding.arena.storage.framework.ConfigurationMapperReflectionUtil;
import agency.shitcoding.arena.storage.framework.ConfigurationMapperTypeValidator;
import agency.shitcoding.arena.storage.framework.annotation.MappedField;
import java.util.List;
import lombok.*;
import org.junit.Test;

public class ConfigurationMapperTest {
  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class IntListTestClass {
    private List<Integer> integerList;
  }

  @Setter
  public static class GetterlessTestClass implements ConfigurationMappable {
    @MappedField() private String string;
    @Getter private String id;
  }

  @Getter
  public static class SetterlessTestClass implements ConfigurationMappable {
    @SuppressWarnings("unused")
    @MappedField
    private String string;

    @Setter private String id;
  }

  @Test
  public void testGenericExtractor() {
    var field = IntListTestClass.class.getDeclaredFields()[0];
    var genericType = ConfigurationMapperReflectionUtil.extractGenericType(field);
    assert genericType.equals(Integer.class);
  }

  @Test
  public void testGetterlessClassValidationFails() {
    var validation =
        new ConfigurationMapperTypeValidator(GetterlessTestClass.class).validateStructure();

    assertTrue(validation.isInvalid());
    assertTrue(validation.getError().getMessage().contains("getter"));
  }

  @Test
  public void testSetterlessClassValidationFails() {
    var validation =
        new ConfigurationMapperTypeValidator(SetterlessTestClass.class).validateStructure();

    assertTrue(validation.isInvalid());
    assertTrue(validation.getError().getMessage().contains("setter"));
  }
}
