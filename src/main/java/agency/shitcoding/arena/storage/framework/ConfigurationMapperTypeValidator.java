package agency.shitcoding.arena.storage.framework;

import static agency.shitcoding.arena.storage.framework.ConfigurationMapperReflectionUtil.*;

import io.vavr.control.Validation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jspecify.annotations.Nullable;

public class ConfigurationMapperTypeValidator {
  private final Class<? extends ConfigurationMappable> type;
  private final Pattern fieldNamePattern = Pattern.compile("^\\w+$");

  public ConfigurationMapperTypeValidator(Class<? extends ConfigurationMappable> type) {
    this.type = type;
  }

  public Validation<ConfigurationMapperValidationException, @Nullable Void> validateStructure() {
    Field[] declaredFields = type.getDeclaredFields();
    Set<String> fieldNames = new HashSet<>();
    for (Field field : declaredFields) {
      if (!fieldFilter(field)) {
        continue;
      }
      var fieldName = getFieldName(field);
      if (!fieldNames.add(fieldName)) {
        return Validation.invalid(
            new ConfigurationMapperValidationException(
                "Field name " + fieldName + " is not unique"));
      }
      if (!fieldNamePattern.matcher(fieldName).matches()) {
        return Validation.invalid(
            new ConfigurationMapperValidationException(
                "Field name " + fieldName + " is not allowed"));
      }
      if (getGetter(type, field) == null) {
        return Validation.invalid(
            new ConfigurationMapperValidationException(
                "No getter found for field " + field.getName()));
      }
      if (getSetter(type, field) == null) {
        return Validation.invalid(
            new ConfigurationMapperValidationException(
                "No setter found for field " + field.getName()));
      }

      // Now we have to determine whether we can actually put the field into the configuration
      // So there are three rules:
      // 1. The field is a primitive type
      // 2. The field is a ConfigurationSerializable (bukkit)
      // 3. The field is a ConfigurationMappable
      // 4. The field is an Enum
      // If the field is a collection or array (We support only lists, sets and arrays),
      // then we should apply the same check to the generic of the field
      if (isPrimitiveOrWrappedPrimitive(field.getType())) {
        continue;
      }
      if (ConfigurationSerializable.class.isAssignableFrom(field.getType())) {
        continue;
      }
      if (ConfigurationMappable.class.isAssignableFrom(field.getType())) {
        continue;
      }
      if (field.getType().isEnum()) {
        continue;
      }
      if (isSupportedCollectionLike(field)) {
        Class<?> genericType = extractGenericType(field);
        if (isPrimitiveOrWrappedPrimitive(genericType)) {
          continue;
        }
        if (isConfigurationSerializable(genericType)) {
          continue;
        }
        if (isConfigurationMappable(genericType)) {
          continue;
        }
        if (genericType.isEnum()) {
          continue;
        }
        return Validation.invalid(
            new ConfigurationMapperValidationException(
                "Field "
                    + field.getName()
                    + " is a collection like type, but the generic type is not supported"));
      }
      return Validation.invalid(
          new ConfigurationMapperValidationException(
              "Field " + field.getName() + " is not a supported type"));
    }
    return Validation.valid(null);
  }
}
