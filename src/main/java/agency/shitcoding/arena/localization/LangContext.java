package agency.shitcoding.arena.localization;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import agency.shitcoding.arena.gamestate.announcer.AnnouncerConstant;
import agency.shitcoding.arena.SoundConstants;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public class LangContext implements Announcer {
  public static final LangContext DEFAULT = new LangContext(SupportedLocale.EN);

  private final String locale;
  private  SupportedLocale supportedLocale = null;

  public LangContext(String locale) {
    this.locale = locale;
  }
  public LangContext(Locale locale) {
    this.locale = locale.getLanguage();
  }
  public LangContext(SupportedLocale locale) {
    this.locale = locale.getLocale();
    this.supportedLocale = locale;
  }

  public LangContext() {
    this.locale = LocalizationService.getInstance().getDefaultLocale();
  }

  public String getLocalized(String key, Object... args) {
    return LocalizationService.getInstance().getLocalized(key, locale, args);
  }

  public static final List<String> SUPPORTED_SOUNDS = List.of("en", "ru", "et", "uk", "pt");

  @Override
  public String translateAnnounce(AnnouncerConstant constant) {
    if (!SUPPORTED_SOUNDS.contains(locale)) {
      return SoundConstants.ANNOUNCE_BASE + "en." + constant.getSoundName();
    }
    return SoundConstants.ANNOUNCE_BASE + locale + "." + constant.getSoundName();
  }

  public Component getRichLocalized(String key, Object... args) {
    return miniMessage().deserialize(getLocalized(key, args));
  }


  public SupportedLocale getSupportedLocale() {
    if (supportedLocale == null) {
      supportedLocale = SupportedLocale.fromString(locale);
    }

    return supportedLocale;
  }
}
