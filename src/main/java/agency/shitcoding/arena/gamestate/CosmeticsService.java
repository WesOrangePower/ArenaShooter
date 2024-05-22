package agency.shitcoding.arena.gamestate;

import static org.bukkit.persistence.PersistentDataType.STRING;

import agency.shitcoding.arena.WeaponItemGenerator;
import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.Weapon;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class CosmeticsService {

  public static final Map<Weapon, NamespacedKey[]> weaponMods = new EnumMap<>(Weapon.class);

  static {
    weaponMods.put(Weapon.ROCKET_LAUNCHER, new NamespacedKey[] {Keys.getKittyCannonKey()});
  }

  private final Map<Player, ItemStack[]> playerWeapons;

  private CosmeticsService() {
    this.playerWeapons = new HashMap<>();
  }

  private static CosmeticsService instance = null;

  public static CosmeticsService getInstance() {
    if (instance == null) {
      instance = new CosmeticsService();
    }
    return instance;
  }

  public ItemStack getWeapon(Player player, Weapon weaponType) {

    if (!this.playerWeapons.containsKey(player)) {
      this.playerWeapons.put(player, getPlayerWeapons(player));
    }

    return this.playerWeapons.get(player)[weaponType.ordinal()];
  }

  public void dropCache() {
    this.playerWeapons.clear();
  }

  public void dropCache(Player player) {
    this.playerWeapons.remove(player);
  }

  public List<String> getAvailableWeaponMods(Player player, Weapon weapon) {
    List<String> items = new ArrayList<>();

    for (NamespacedKey key : weaponMods.getOrDefault(weapon, new NamespacedKey[0])) {
      if (player.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN)) {
        items.add(key.getKey());
      }
    }

    return items;
  }

  private ItemStack[] getPlayerWeapons(Player player) {

    ItemStack[] weapons = new ItemStack[Weapon.values().length];

    for (Weapon weapon : Weapon.values()) {
      weapons[weapon.ordinal()] = WeaponItemGenerator.generate(player, weapon);
    }

    return weapons;
  }

  public @Nullable String getWeaponMod(Player player, Weapon weapon) {
    var pdc = player.getPersistentDataContainer();
    var key = Keys.ofWeapon(weapon);
    if (pdc.has(key, STRING)) {
      return pdc.get(key, STRING);
    }
    return null;
  }
}
