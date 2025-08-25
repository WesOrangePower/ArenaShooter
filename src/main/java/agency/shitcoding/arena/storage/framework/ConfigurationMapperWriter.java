package agency.shitcoding.arena.storage.framework;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static agency.shitcoding.arena.storage.framework.ConfigurationMapperReflectionUtil.*;

class ConfigurationMapperWriter {
  private final Configuration bukkitConfiguration;

  ConfigurationMapperWriter(Configuration bukkitConfiguration) {
    this.bukkitConfiguration = bukkitConfiguration;
  }

  void write(@Nullable String path, ConfigurationMappable object) {
    var section = getOrCreateSection(bukkitConfiguration, path);
    var subSection = getOrCreateSection(section, object.getId());

    for (var field : object.getClass().getDeclaredFields()) {
      if (!fieldFilter(field)) {
        continue;
      }
      var fieldName = getFieldName(field);
      if (isPrimitiveOrWrappedPrimitive(field.getType())) {
        var fieldValueEither = getFieldValue(object, field);
        if (fieldValueEither.isLeft()) throw fieldValueEither.getLeft();
        var fieldValue = fieldValueEither.get();
        subSection.set(fieldName, fieldValue);
        continue;
      }
      if (isSupportedCollectionLike(field)) {
        var type = extractGenericType(field);
        var fieldValueEither = getFieldValue(object, field);
        if (fieldValueEither.isLeft()) throw fieldValueEither.getLeft();

        if (isPrimitiveOrWrappedPrimitive(type)) {
          var fieldValue = fieldValueEither.get();
          subSection.set(fieldName, fieldValue);
          continue;
        }
        if (type.isEnum()) {
          List<Enum<?>> list =
              collectionLikeToList(getFieldValue(object, field).getOrElseThrow(x -> x));
          List<String> strList = list.stream().map(Enum::name).toList();
          subSection.set(fieldName, strList);
          continue;
        }
        var listSubSection = getOrCreateSection(subSection, fieldName);
        if (isConfigurationMappable(type)) {
          List<?> list = collectionLikeToList(getFieldValue(object, field).getOrElseThrow(x -> x));
          for (var item : list) {
            write(listSubSection.getCurrentPath(), (ConfigurationMappable) item);
          }
          continue;
        }
        if (isConfigurationSerializable(type)) {
          List<?> list = collectionLikeToList(getFieldValue(object, field).getOrElseThrow(x -> x));
          int i = 0;
          for (var item : list) {
            listSubSection.set(listSubSection.getCurrentPath() + "." + i++, item);
          }
          continue;
        }
        throw new ConfigurationMapperValidationException("Unsupported collection like type");
      }
      if (isConfigurationMappable(field.getType())) {
        var fieldValueEither = getFieldValue(object, field);
        if (fieldValueEither.isLeft()) throw fieldValueEither.getLeft();
        write(
            subSection.getCurrentPath() + "." + fieldName,
            (ConfigurationMappable) fieldValueEither.get());
        continue;
      }
      if (isConfigurationSerializable(field.getType())) {
        var fieldValueEither = getFieldValue(object, field);
        if (fieldValueEither.isLeft()) throw fieldValueEither.getLeft();
        subSection.set(fieldName, fieldValueEither.get());
        continue;
      }
      if (field.getType().isEnum()) {
        var fieldValueEither = getFieldValue(object, field);
        if (fieldValueEither.isLeft()) throw fieldValueEither.getLeft();
        subSection.set(fieldName, ((Enum<?>) fieldValueEither.get()).name());
        continue;
      }
      throw new ConfigurationMapperValidationException("Unsupported field type");
    }
  }

  public static ConfigurationSection getOrCreateSection(
      ConfigurationSection section, @Nullable String path) {
    if (path == null) {
      return section;
    }
    var subSection = section.getConfigurationSection(path);
    if (subSection == null) {
      subSection = section.createSection(path);
    }
    return subSection;
  }
}
