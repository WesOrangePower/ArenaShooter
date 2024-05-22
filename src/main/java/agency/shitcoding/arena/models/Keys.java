package agency.shitcoding.arena.models;

import agency.shitcoding.arena.ArenaShooter;
import org.bukkit.NamespacedKey;

public final class Keys {

  public static final NamespacedKey LOOT_POINT_KEY = new NamespacedKey(ArenaShooter.getInstance(),
      "lootPointId");


  public static NamespacedKey getPlayerAmmoKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "ammoValues");
  }

  public static NamespacedKey getPlayerLocalizationKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "locale");
  }

  public static NamespacedKey getKittyCannonKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "kittyCannon");
  }

  public static NamespacedKey getBubbleGunKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "bubbleGun");
  }

  public static NamespacedKey of(String key) {
    return new NamespacedKey(ArenaShooter.getInstance(), key);
  }

  public static NamespacedKey ofWeapon(Weapon w) {
    return Keys.of(w.name());
  }

  public static NamespacedKey getVectorKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "vector");
  }

  public static NamespacedKey getOwnerKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "owner");
  }
  public static NamespacedKey getVelocityXKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "velocityX");
  }

  public static NamespacedKey getVelocityYKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "velocityY");
  }

  public static NamespacedKey getVelocityZKey() {
    return new NamespacedKey(ArenaShooter.getInstance(), "velocityZ");
  }

  private Keys() {
  }
}
