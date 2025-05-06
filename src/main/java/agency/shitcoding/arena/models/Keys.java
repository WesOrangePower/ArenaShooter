package agency.shitcoding.arena.models;

import agency.shitcoding.arena.ArenaShooter;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public final class Keys {

  // ********************
  // Before you do something stupid again,
  // minecraft keys support only lowercase snake case
  // ********************


  public static NamespacedKey getLootPointKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "loot_point_id");
  }

  public static NamespacedKey getPowerupKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "powerup");
  }

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

  public static @NotNull NamespacedKey getFlagKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "ctf_flag");
  }

  public static @NotNull NamespacedKey noDropOnDeath() {
    return new NamespacedKey(ArenaShooter.getInstance(), "no_drop_on_death");
  }

  private Keys() {
  }
}
