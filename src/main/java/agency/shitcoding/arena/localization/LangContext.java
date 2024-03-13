package agency.shitcoding.arena.localization;

import java.util.Locale;

public class LangContext {

  private final String locale;

  public LangContext(String locale) {
    this.locale = locale;
  }
  public LangContext(Locale locale) {
    this.locale = locale.getLanguage();
  }

  public LangContext() {
    this.locale = LocalizationService.getInstance().getDefaultLocale();
  }

  public String getLocalized(String key, Object... args) {
    return LocalizationService.getInstance().getLocalized(key, locale, args);
  }
}
