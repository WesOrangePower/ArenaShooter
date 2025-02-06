package agency.shitcoding.arena.storage.skips;

import agency.shitcoding.arena.gamestate.announcer.AnnouncementSkip;
import agency.shitcoding.arena.gamestate.announcer.AnnouncerConstant;
import agency.shitcoding.arena.gamestate.announcer.ConstantAnnouncementSkip;
import agency.shitcoding.arena.localization.SupportedLocale;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.EnumMap;

@Slf4j
public class YamlSkipProvider extends FileSkipProvider {
  public YamlSkipProvider(File yamlFile) {
    super(yamlFile);
  }

  @Override
  protected EnumMap<AnnouncerConstant, AnnouncementSkip> parse() {
    var config = YamlConfiguration.loadConfiguration(file);
    var skipMap = new EnumMap<AnnouncerConstant, AnnouncementSkip>(AnnouncerConstant.class);

    var section = config.getConfigurationSection("skips");
    if (section == null) {
      throw new IllegalArgumentException("Root `skips` section not found in " + file.getAbsolutePath());
    }

    for (AnnouncerConstant value : AnnouncerConstant.values()) {
      var skip = section.getConfigurationSection(value.getSoundName());
      if (skip == null) {
        throw new IllegalArgumentException("No skip for " + value);
      }
      var builder = ConstantAnnouncementSkip.builder();
      for (SupportedLocale supportedLocale : SupportedLocale.values()) {
        int skipMilliseconds = skip.getInt(supportedLocale.name());
        if (skipMilliseconds == 0) {
          log.warn("Missing skip time for locale {} in {}", supportedLocale, value);
        }
        builder.fromMillies(supportedLocale, skipMilliseconds);
      }
      builder.addGraceTimeToAll();
      skipMap.put(value, builder.build());
    }

    return skipMap;
  }
}
