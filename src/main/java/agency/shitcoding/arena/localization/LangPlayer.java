package agency.shitcoding.arena.localization;


import agency.shitcoding.arena.models.Keys;
import java.util.Optional;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

@Getter
public class LangPlayer {

  private final Player player;
  private LangContext langContext;

  public LangPlayer(Player player) {
    this.player = player;
    Optional.ofNullable(player).map(
            p -> p
                .getPersistentDataContainer()
                .get(Keys.getPlayerLocalizationKey(), PersistentDataType.STRING)
        )
        .ifPresentOrElse(
            locale -> this.langContext = new LangContext(locale),
            () -> {
              if (player == null) {
                langContext = new LangContext();
                return;
              }
              var locale = player.locale().getLanguage();
              if (!LocalizationService.getInstance().isSupported(locale)) {
                locale = LocalizationService.getInstance().getDefaultLocale();
              }
              setLocale(locale);
            }
        );
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

  public void sendRichLocalized(String key, Object... args) {
    player.sendRichMessage(langContext.getLocalized(key, args));
  }

  public void setLocale(String lang) {
    langContext = new LangContext(lang);
    player.getPersistentDataContainer().set(Keys.getPlayerLocalizationKey(), PersistentDataType.STRING, lang);
  }
}
