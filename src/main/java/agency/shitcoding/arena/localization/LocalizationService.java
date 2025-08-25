package agency.shitcoding.arena.localization;

import agency.shitcoding.arena.models.Keys;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class LocalizationService {
  private static @Nullable LocalizationService instance;
  public static LocalizationService getInstance() {
    if (instance == null) {
      instance = new LocalizationService();
    }
    return instance;
  }

  private final Map<String, ResourceBundle> bundles = new HashMap<>();

  public LocalizationService() {
    for (SupportedLocale locale : SupportedLocale.values()) {
      var localeStr = locale.getLocale();
      bundles.put(localeStr, ResourceBundle.getBundle("localization.arena", Locale.of(localeStr)));
    }
  }

  public String getLocalized(@Nullable String key, @Nullable String locale, Object... args) {
    if (key == null) {
      return "null";
    }
    var bundle = Optional.ofNullable(locale)
        .map(bundles::get)
        .orElse(bundles.get(getDefaultLocale()));

    return bundle.containsKey(key) ? String.format(bundle.getString(key), args) : key;
  }

  @Contract(pure = true, value = "null, null, _ -> param1")
  public String getLocalized(@Nullable String key, @Nullable Player player, Object... args) {

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
    return SupportedLocale.isSupported(locale);
  }
}
