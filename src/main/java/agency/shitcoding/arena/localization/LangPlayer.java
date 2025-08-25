package agency.shitcoding.arena.localization;

import agency.shitcoding.arena.models.Keys;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.i18n.qual.LocalizableKey;

@Getter
public class LangPlayer {

  private final Player player;
  private LangContext langContext;

  public LangPlayer(Player player) {
    this.player = player;
    var locStr =
        player
            .getPersistentDataContainer()
            .get(Keys.getPlayerLocalizationKey(), PersistentDataType.STRING);

    if (locStr != null) {
      langContext = new LangContext(locStr);
      return;
    }
    var locale = player.locale().getLanguage();
    if (!LocalizationService.getInstance().isSupported(locale)) {
      locale = LocalizationService.getInstance().getDefaultLocale();
    }
    setLocale(locale);
  }

  public static LangPlayer of(Player player) {
    return new LangPlayer(player);
  }

  public String getLocalized(String key, Object... args) {
    return langContext.getLocalized(key, args);
  }

  public Component getRichLocalized(String key, Object... args) {
    return langContext.getRichLocalized(key, args);
  }

  public void sendLocalized(String key, Object... args) {
    player.sendMessage(getLocalized(key, args));
  }

  public void sendRichLocalized(@LocalizableKey String key, Object... args) {
    player.sendRichMessage(langContext.getLocalized(key, args));
  }

  public void setLocale(String lang) {
    langContext = new LangContext(lang);
    player
        .getPersistentDataContainer()
        .set(Keys.getPlayerLocalizationKey(), PersistentDataType.STRING, lang);
  }
}
