package agency.shitcoding.arena.localization;

import agency.shitcoding.arena.models.Keys;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LocalizationService {
  private static LocalizationService instance;
  public static LocalizationService getInstance() {
    if (instance == null) {
      instance = new LocalizationService();
    }
    return instance;
  }

  public static final String [] SUPPORTED_LOCALES = { "en", "ru", "et", "uk", "pt" };
  private final Map<String, ResourceBundle> bundles = new HashMap<>();

  public LocalizationService() {
    for (String locale : SUPPORTED_LOCALES) {
      bundles.put(locale, ResourceBundle.getBundle("localization.arena", Locale.of(locale)));
    }
  }

  public @NotNull String getLocalized(@Nullable String key, @Nullable String locale, Object... args) {
    if (key == null) {
      return "null";
    }
    var bundle = Optional.ofNullable(locale)
        .map(bundles::get)
        .orElse(bundles.get(getDefaultLocale()));

    return bundle.containsKey(key) ? String.format(bundle.getString(key), args) : key;
  }

  @Contract(pure = true, value = "null, null, _ -> param1")
  public @NotNull String getLocalized(@Nullable String key, @Nullable Player player, Object... args) {

    var locale = Optional.ofNullable(player)
        .map(
            p -> p.getPersistentDataContainer().get(Keys.getPlayerLocalizationKey(),
                PersistentDataType.STRING)
        ).orElse(getDefaultLocale());

    return getLocalized(key, locale, args);
  }

  public String getDefaultLocale() {
    return "en";
  }

  public boolean isSupported(String locale) {
    for (String supportedLocale : SUPPORTED_LOCALES) {
      if (supportedLocale.equals(locale)) {
        return true;
      }
    }
    return false;
  }
}
