package agency.shitcoding.arena.localization;

import agency.shitcoding.arena.AnnouncerConstant;
import agency.shitcoding.arena.SoundConstants;
import java.util.List;
import java.util.Locale;
import lombok.Getter;

@Getter
public class LangContext implements Announcer {

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

  public static final List<String> SUPPORTED_SOUNDS = List.of("en", "ru", "et", "uk");

  @Override
  public String translateAnnounce(AnnouncerConstant constant) {
    if (!SUPPORTED_SOUNDS.contains(locale)) {
      return SoundConstants.ANNOUNCE_BASE + "en." + constant.getSoundName();
    }
    return SoundConstants.ANNOUNCE_BASE + locale + "." + constant.getSoundName();
  }
}
