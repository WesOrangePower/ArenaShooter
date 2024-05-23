package agency.shitcoding.arena.storage;

import java.util.List;
import java.util.Map;

public interface CosmeticsStorage {
  void storeWeaponMod(String playerName, String weaponMod);

  List<String> getWeaponMods(String playerName);

  void deleteWeaponMod(String playerName, String weaponMod);

  void refresh();

  Map<String, List<String>> getAllWeaponMods();
}
