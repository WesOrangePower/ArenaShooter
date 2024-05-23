package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.command.Conf;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ConfigurationCosmeticsStorage implements CosmeticsStorage {

  public static final File FILE =
      new File(ArenaShooter.getInstance().getDataFolder(), "cosmetics.db.yml");

  private final Configuration configuration;

  @Override
  public void storeWeaponMod(String playerName, String weaponMod) {
    var playerSection = getPlayerSection(playerName);
    List<String> mods = new ArrayList<>(playerSection.getStringList("mods"));
    mods.add(weaponMod);
    playerSection.set("mods", mods);
    save();
  }

  @Override
  public List<String> getWeaponMods(String playerName) {
    var playerSection = getPlayerSection(playerName);
    return playerSection.getStringList("mods");
  }

  @Override
  public void deleteWeaponMod(String playerName, String weaponMod) {
    var playerSection = getPlayerSection(playerName);
    var mods = playerSection.getStringList("mods");
    var copy = new ArrayList<>(mods);
    copy.removeIf(mod -> mod.equals(weaponMod));
    playerSection.set("mods", copy);
    save();
  }

  @Override
  public void refresh() {
    if (configuration instanceof YamlConfiguration yamlConfiguration) {
      try {
        yamlConfiguration.load(FILE);
      } catch (Exception e) {
        ArenaShooter.getInstance().getLogger().severe("Failed to load " + FILE);
      }
    }
  }

  private ConfigurationSection getPlayerSection(String playerName) {
    var weaponModSection = configuration.getConfigurationSection(Conf.weaponModSection);
    if (weaponModSection == null) {
      weaponModSection = configuration.createSection(Conf.weaponModSection);
    }

    var playerSection = weaponModSection.getConfigurationSection(playerName);
    if (playerSection == null) {
      playerSection = weaponModSection.createSection(playerName);
    }

    return playerSection;
  }

  private void save() {
    if (configuration instanceof YamlConfiguration yamlConfiguration) {
      try {
        yamlConfiguration.save(FILE);
      } catch (Exception e) {
        ArenaShooter.getInstance().getLogger().severe("Failed to save " + FILE);
      }
    }
  }
}
