package agency.shitcoding.arena.gamestate;

import static org.bukkit.persistence.PersistentDataType.STRING;

import agency.shitcoding.arena.WeaponItemGenerator;
import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.Weapon;
import agency.shitcoding.arena.storage.StorageProvider;
import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

public class CosmeticsService {

  private final Map<Weapon, WeaponMod[]> weaponMods;

  private final Map<Player, ItemStack[]> playerWeapons;

  private CosmeticsService() {
    this.playerWeapons = new WeakHashMap<>();
    weaponMods =
        Arrays.stream(WeaponMods.REGISTRY)
            .collect(
                Collectors.groupingBy(
                    WeaponMod::weapon,
                    () -> new EnumMap<>(Weapon.class),
                    Collectors.collectingAndThen(
                        Collectors.toList(), list -> list.toArray(WeaponMod[]::new))));
  }

  private static @Nullable CosmeticsService instance = null;

  public static CosmeticsService getInstance() {
    if (instance == null) {
      instance = new CosmeticsService();
    }
    return instance;
  }

  public void addWeaponMod(Player player, WeaponMod weaponMod) {
    if (getAvailableWeaponMods(player, weaponMod.weapon()).contains(weaponMod)) {
      return;
    }
    StorageProvider.getCosmeticsStorage().storeWeaponMod(player.getName(), weaponMod.mod());
    dropCache(player);
  }

  public void addWeaponMod(String playerName, WeaponMod weaponMod) {
    Player player = Bukkit.getPlayerExact(playerName);
    if (player != null) {
      addWeaponMod(player, weaponMod);
      return;
    }
    StorageProvider.getCosmeticsStorage().storeWeaponMod(playerName, weaponMod.mod());
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

  public List<WeaponMod> getAvailableWeaponMods(Player player, Weapon weapon) {
    List<WeaponMod> items = new ArrayList<>();

    for (WeaponMod weaponMod : weaponMods.getOrDefault(weapon, new WeaponMod[0])) {
      if (hasMod(player, weaponMod)) {
        items.add(weaponMod);
      }
    }

    return items;
  }

  public boolean hasMod(Player player, WeaponMod weaponMod) {
    var mods = StorageProvider.getCosmeticsStorage().getWeaponMods(player.getName());
    return mods.contains(weaponMod.mod());
  }

  private ItemStack[] getPlayerWeapons(Player player) {

    ItemStack[] weapons = new ItemStack[Weapon.values().length];

    for (Weapon weapon : Weapon.values()) {
      weapons[weapon.ordinal()] = WeaponItemGenerator.generate(player, weapon);
    }

    return weapons;
  }

  public @Nullable String getWeaponModName(Player player, Weapon weapon) {
    var pdc = player.getPersistentDataContainer();
    var key = Keys.ofWeapon(weapon);
    if (pdc.has(key, STRING)) {
      return pdc.get(key, STRING);
    }
    return null;
  }

  public @Nullable WeaponMod getWeaponMod(Player player, Weapon weapon) {
    var val = getWeaponModName(player, weapon);
    return val == null ? null : new WeaponMod(weapon, val);
  }

  @SuppressWarnings("NullableProblems")
  public List<WeaponMod> getAllAvailableWeaponMods(String playerName) {
    return StorageProvider.getCosmeticsStorage().getWeaponMods(playerName)
        .stream()
        .map(WeaponMods::findByName)
        .filter(Objects::nonNull)
        .toList();
  }

  public void removeWeaponMod(String playerName, WeaponMod weaponMod) {
    StorageProvider.getCosmeticsStorage().deleteWeaponMod(playerName, weaponMod.mod());
  }
}
