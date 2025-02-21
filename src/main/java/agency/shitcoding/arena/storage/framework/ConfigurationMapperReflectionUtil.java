package agency.shitcoding.arena.storage.framework;

import agency.shitcoding.arena.storage.framework.annotation.MappedField;
import io.vavr.control.Either;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.vavr.control.Try;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigurationMapperReflectionUtil {
  public static Class<?> extractGenericType(Field field) {
    if (!isSupportedCollectionLike(field)) {
      throw new IllegalArgumentException("Field is not a supported collection like type");
    }
    if (field.getType().isArray()) {
      return field.getType().getComponentType();
    }

    if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
      return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }
    throw new IllegalArgumentException("Field is not a supported collection like type");
  }

  public static boolean isSupportedCollectionLike(Field field) {
    Class<?> type = field.getType();
    return type.isArray() || List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type);
  }

  public static Either<ConfigurationMapperValidationException, Object> listToSupportedCollection(
      Field field, List<?> list) {
    Class<?> type = field.getType();
    if (type.isArray()) {
      return Either.right(list.toArray());
    }
    if (List.class.isAssignableFrom(type)) {
      return Either.right(list);
    }
    if (Set.class.isAssignableFrom(type)) {
      return Either.right(new HashSet<>(list));
    }
    return Either.left(
        new ConfigurationMapperValidationException(
            "Unsupported collection type: " + type.getName()));
  }

  @SuppressWarnings("unchecked")
  public static <T> List<T> collectionLikeToList(Object collectionLike) {
    if (collectionLike instanceof List<?> list) {
      return (List<T>) list;
    }
    if (collectionLike instanceof Set<?> set) {
      return new ArrayList<>((Set<? extends T>) set);
    }
    if (collectionLike.getClass().isArray()) {
      return List.of((T[]) collectionLike);
    }
    throw new IllegalArgumentException("Collection like object is not a supported type");
  }

  public static boolean isPrimitiveOrWrappedPrimitive(Class<?> type) {
    return type.isPrimitive()
        || type.equals(Integer.class)
        || type.equals(Double.class)
        || type.equals(Float.class)
        || type.equals(Boolean.class)
        || type.equals(Long.class)
        || type.equals(Byte.class)
        || type.equals(Short.class)
        || type.equals(String.class); // String is supported as well;
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean fieldFilter(Field field) {
    return field.isAnnotationPresent(MappedField.class);
  }

  public static Either<ConfigurationMapperValidationException, Object> getFieldValue(
      Object o, Field field) {
    Method getter = getGetter(o.getClass(), field);
    if (getter == null) {
      return Either.left(
          new ConfigurationMapperValidationException(
              "No getter found for field " + field.getName()));
    }
    try {
      return Either.right(getter.invoke(o));
    } catch (InvocationTargetException | IllegalAccessException e) {
      return Either.left(
          new ConfigurationMapperValidationException(
              "Error while invoking getter for field " + field.getName(), e));
    }
  }

  public static Either<ConfigurationMapperValidationException, Void> setFieldValue(
      Object o, Field field, Object value) {
    Method setter = getSetter(o.getClass(), field);
    if (setter == null) {
      return Either.left(
          new ConfigurationMapperValidationException(
              "No setter found for field" + field.getName()));
    }
    try {
      setter.invoke(o, value);
      return Either.right(null);
    } catch (InvocationTargetException | IllegalAccessException e) {
      return Either.left(
          new ConfigurationMapperValidationException(
              "Error while invoking setter for field " + field.getName(), e));
    }
  }

  public static String getFieldName(Field field) {
    MappedField annotation = field.getAnnotation(MappedField.class);
    if (annotation != null && !annotation.value().isEmpty()) {
      return annotation.value();
    }
    return field.getName();
  }

  public static @Nullable Method getGetter(Class<?> parentClass, Field field) {
    Class<?> type = field.getType();
    boolean isBoolean = type.equals(boolean.class) || type.equals(Boolean.class);
    String fieldName = field.getName();
    String getterName;
    if (isBoolean) {
      getterName = "is";
    } else {
      getterName = "get";
    }
    if (fieldName.startsWith("is") && fieldName.length() >= 3) {
      getterName += fieldName.substring(2, 3).toUpperCase() + fieldName.substring(3);
    } else {
      getterName += fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    try {
      var getterLike = parentClass.getMethod(getterName);
      if (getterLike.getReturnType().equals(type)) {
        return getterLike;
      }
      return null;
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  public static @Nullable Method getSetter(Class<?> parentClass, Field field) {
    Class<?> type = field.getType();
    String fieldName = field.getName();
    boolean isBoolean = type.equals(boolean.class) || type.equals(Boolean.class);
    String setterName;
    if (isBoolean && fieldName.startsWith("is") && fieldName.length() >= 3) {
      setterName = "set" + fieldName.substring(2, 3).toUpperCase() + fieldName.substring(3);
    } else {
      setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
    try {
      var setterLike = parentClass.getMethod(setterName, type);
      if (setterLike.getReturnType().equals(void.class)) {
        return setterLike;
      }
      return null;
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  public static boolean isConfigurationMappable(Class<?> type) {
    return ConfigurationMappable.class.isAssignableFrom(type);
  }

  public static boolean isConfigurationSerializable(Class<?> type) {
    return ConfigurationSerializable.class.isAssignableFrom(type);
  }

  public static <T> Either<ConfigurationMapperValidationException, T> createInstanceWithNac(
      Class<T> tClass) {
    return Try.of(() -> tClass.getDeclaredConstructor().newInstance())
        .fold(
            x ->
                Either.left(
                    new ConfigurationMapperValidationException(
                        "Cannot instantiate " + tClass.getName(), x)),
            Either::right);
  }
}
