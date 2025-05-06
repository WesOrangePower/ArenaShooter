package agency.shitcoding.arena.localization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum SupportedLocale {
  EN("en"),
  RU("ru"),
  ET("et"),
  UK("uk"),
  PT("pt");

  private final String locale;

  public static SupportedLocale fromString(String locale) {
    for (SupportedLocale supportedLocale : SupportedLocale.values()) {
      if (supportedLocale.getLocale().equals(locale)) {
        return supportedLocale;
      }
    }
    return EN;
  }

  public static SupportedLocale fromLocale(Locale locale) {
    return fromString(locale.getLanguage());
  }

  public static boolean isSupported(String locale) {
    for (SupportedLocale supportedLocale : SupportedLocale.values()) {
      if (supportedLocale.getLocale().equals(locale)) {
        return true;
      }
    }
    return false;
  }

  public static SupportedLocale[] ALL = values();

  public static SupportedLocale getDefault() {
    return EN;
  }
}
