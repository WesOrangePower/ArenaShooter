package agency.shitcoding.arena.storage;

import java.util.List;

public interface CosmeticsStorage {
  void storeWeaponMod(String playerName, String weaponMod);

  List<String> getWeaponMods(String playerName);

  void deleteWeaponMod(String playerName, String weaponMod);
}
