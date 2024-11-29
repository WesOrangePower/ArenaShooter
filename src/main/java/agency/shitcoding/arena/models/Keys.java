package agency.shitcoding.arena.models;

import agency.shitcoding.arena.ArenaShooter;
import org.bukkit.NamespacedKey;

public final class Keys {

  // ********************
  // Before you do something stupid again,
  // minecraft keys support only lowercase snake case
  // ********************

  public static final NamespacedKey LOOT_POINT_KEY = new NamespacedKey(ArenaShooter.getInstance(),
      "loot_point_id");


  public static NamespacedKey getPlayerAmmoKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "ammo_values");
  }

  public static NamespacedKey getPlayerLocalizationKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "locale");
  }


  public static NamespacedKey of(String key) {
    return new NamespacedKey(ArenaShooter.getInstance(), key);
  }

  public static NamespacedKey ofWeapon(Weapon w) {
    return Keys.of(w.name());
  }

  public static NamespacedKey getIgnoreHitKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "ignore_hit");
  }

  private Keys() {
  }
}
