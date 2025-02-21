package agency.shitcoding.arena.storage.framework;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static agency.shitcoding.arena.storage.framework.ConfigurationMapperReflectionUtil.*;

class ConfigurationMapperReader<T extends ConfigurationMappable> {
  private final Class<T> type;
  private final Configuration bukkitConfiguration;

  ConfigurationMapperReader(Class<T> type, Configuration bukkitConfiguration) {
    this.type = type;
    this.bukkitConfiguration = bukkitConfiguration;
  }

  T readMappable(String rootPath) {
    return read(rootPath, type);
  }

  private <U extends ConfigurationMappable> U read(String rootPath, Class<U> tType) {
    ConfigurationSection section = bukkitConfiguration.getConfigurationSection(rootPath);
    if (section == null) {
      throw new IllegalArgumentException("Section " + rootPath + " is null");
    }
    U obj = ConfigurationMapperReflectionUtil.createInstanceWithNac(tType).getOrElseThrow(l -> l);
    String currentPath = section.getCurrentPath();
    if (currentPath == null) {
      throw new IllegalStateException("Cannot get current path of section at hand");
    }
    String[] split = currentPath.split("\\.");
    String id = split[split.length - 1];
    obj.setId(id);

    for (Field field : tType.getDeclaredFields()) {
      if (!fieldFilter(field)) {
        continue;
      }

      var fieldName = getFieldName(field);
      if (isPrimitiveOrWrappedPrimitive(field.getType())) {
        readPrimitive(section, fieldName, obj, field);
        continue;
      }
      if (field.getType().isEnum()) {
        readEnum(section, fieldName, obj, field);
        continue;
      }
      if (isConfigurationSerializable(field.getType())) {
        readConfigurationSerializable(section, fieldName, obj, field);
        continue;
      }
      if (isConfigurationMappable(field.getType())) {
        var path = currentPath + "." + fieldName;
        var configurationSection = bukkitConfiguration.getConfigurationSection(path);
        if (configurationSection == null) {
          throw new IllegalArgumentException("Section for field " + fieldName + " is null");
        }
        for (String key : configurationSection.getKeys(false)) {
          //noinspection unchecked
          read(
              configurationSection.getCurrentPath() + "." + key,
              (Class<? extends ConfigurationMappable>) field.getType());
          break;
        }
        continue;
      }
      if (isSupportedCollectionLike(field)) {
        readSupportedCollectionLike(section, fieldName, obj, field);
        continue;
      }
      throw new IllegalStateException(
          "Unsupported type: " + field.getType().getName() + " at name " + fieldName);
    }

    return obj;
  }

  private void readSupportedCollectionLike(
      ConfigurationSection section, String fieldName, ConfigurationMappable obj, Field field) {

    List<?> list;
    var genType = extractGenericType(field);
    if (isPrimitiveOrWrappedPrimitive(genType)) {
      list = readPrimitiveList(section, fieldName, field);
    } else if (genType.isEnum()) {
      list = readEnumList(section, fieldName, field);
    } else if (isConfigurationSerializable(genType)) {
      list = readConfigurationSerializableList(section, fieldName, field);
    } else if (isConfigurationMappable(genType)) {
      list = readConfigurationMappableList(section, fieldName, field);
    } else {
      throw new IllegalStateException("Cannot read collection of unsupported type");
    }
    var collection = listToSupportedCollection(field, list).getOrElseThrow(x -> x);
    setFieldValue(obj, field, collection).getOrElseThrow(x -> x);
  }

  private <U extends ConfigurationMappable> List<U> readConfigurationMappableList(
      ConfigurationSection section, String fieldName, Field field) {
    var listSection = section.getConfigurationSection(fieldName);
    if (listSection == null) {
      return new ArrayList<>();
    }
    List<U> list = new ArrayList<>();
    //noinspection unchecked
    var genType = (Class<U>) extractGenericType(field);
    for (String key : listSection.getKeys(false)) {
      U u = read(listSection.getCurrentPath() + "." + key, genType);
      list.add(u);
    }
    return list;
  }

  private <U extends ConfigurationSerializable> List<U> readConfigurationSerializableList(
      ConfigurationSection section, String fieldName, Field field) {
    List<U> list = new ArrayList<>();
    var listSection = section.getConfigurationSection(fieldName);
    if (listSection == null) throw new IllegalStateException();

    //noinspection unchecked
    var genType = (Class<U>) extractGenericType(field);
    for (String key : listSection.getKeys(false)) {
      list.add(listSection.getSerializable(key, genType));
    }
    return list;
  }

  private <E extends Enum<E>> List<E> readEnumList(
      ConfigurationSection section, String fieldName, Field field) {
    //noinspection unchecked
    var enumClass = (Class<E>) extractGenericType(field);
    return section.getStringList(fieldName).stream().map(s -> Enum.valueOf(enumClass, s)).toList();
  }

  private List<?> readPrimitiveList(ConfigurationSection section, String fieldName, Field field) {
    Class<?> genType = extractGenericType(field);
    if (genType.equals(boolean.class) || genType.equals(Boolean.class)) {
      return section.getBooleanList(fieldName);
    }
    if (genType.equals(Double.class) || genType.equals(double.class)) {
      return section.getIntegerList(fieldName);
    }
    if (genType.equals(Integer.class) || genType.equals(int.class)) {
      return section.getIntegerList(fieldName);
    }
    if (genType.equals(Float.class) || genType.equals(float.class)) {
      return section.getFloatList(fieldName);
    }
    if (genType.equals(Long.class) || genType.equals(long.class)) {
      return section.getLongList(fieldName);
    }
    if (genType.equals(Byte.class) || genType.equals(byte.class)) {
      return section.getLongList(fieldName);
    }
    if (genType.equals(Short.class) || genType.equals(short.class)) {
      return section.getShortList(fieldName);
    }
    if (genType.equals(String.class)) {
      return section.getStringList(fieldName);
    }
    throw new IllegalStateException(
        "readPrimitiveList called on unknown type: " + genType.getName());
  }

  private void readConfigurationSerializable(
      ConfigurationSection base, String name, ConfigurationMappable obj, Field field) {
    var cs = base.get(name);
    setFieldValue(obj, field, cs).getOrElseThrow(x -> x);
  }

  private <E extends Enum<E>> void readEnum(
      ConfigurationSection base, String name, ConfigurationMappable obj, Field field) {
    if (!field.getType().isEnum()) {
      throw new IllegalStateException("readEnum called on non-enum field");
    }
    String string = base.getString(name);
    if (string == null) {
      throw new NullPointerException();
    }
    //noinspection unchecked
    Class<E> enumClass = (Class<E>) field.getType();
    E e = Enum.valueOf(enumClass, string);
    setFieldValue(obj, field, e).getOrElseThrow(x -> x);
  }

  private void readPrimitive(
      ConfigurationSection section, String name, ConfigurationMappable obj, Field field) {
    Class<?> fieldType = field.getType();
    if (!isPrimitiveOrWrappedPrimitive(fieldType)) {
      throw new IllegalStateException("readPrimitive called on non-primitive field");
    }
    if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
      setFieldValue(obj, field, section.getBoolean(name)).getOrElseThrow(x -> x);
      return;
    }
    if (Integer.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType)) {
      setFieldValue(obj, field, section.getInt(name)).getOrElseThrow(x -> x);
    }
    if (Float.class.isAssignableFrom(fieldType) || float.class.isAssignableFrom(fieldType)) {
      setFieldValue(obj, field, (float) section.getDouble(name)).getOrElseThrow(x -> x);
    }
    if (Double.class.isAssignableFrom(fieldType) || double.class.isAssignableFrom(fieldType)) {
      setFieldValue(obj, field, section.getDouble(name)).getOrElseThrow(x -> x);
    }
    if (Long.class.isAssignableFrom(fieldType) || double.class.isAssignableFrom(fieldType)) {
      setFieldValue(obj, field, section.getLong(name)).getOrElseThrow(x -> x);
    }
    if (Byte.class.isAssignableFrom(fieldType) || byte.class.isAssignableFrom(fieldType)) {
      setFieldValue(obj, field, (byte) section.getInt(name)).getOrElseThrow(x -> x);
    }
    if (Character.class.isAssignableFrom(fieldType) || char.class.isAssignableFrom(fieldType)) {
      String string = section.getString(name);
      char c = '\0';
      if (string != null) c = string.charAt(0);
      setFieldValue(obj, field, c).getOrElseThrow(x -> x);
    }
    if (Short.class.isAssignableFrom(fieldType) || short.class.isAssignableFrom(fieldType)) {
      setFieldValue(obj, field, (short) section.getInt(name)).getOrElseThrow(x -> x);
    }
    if (String.class.isAssignableFrom(fieldType)) {
      setFieldValue(obj, field, section.getString(name)).getOrElseThrow(x -> x);
    }
  }
}
