package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Weapon;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class WeaponMods {
  private static final WeaponMod BUBBLE_GUN = new WeaponMod(Weapon.RAILGUN, "bubble_gun");
  private static final WeaponMod KITTY_CANNON = new WeaponMod(Weapon.ROCKET_LAUNCHER, "kitty_cannon");
  private static final WeaponMod SLIMA_GUN = new WeaponMod(Weapon.PLASMA_GUN, "slima_gun");

  public static final WeaponMod[] REGISTRY = {BUBBLE_GUN, KITTY_CANNON, SLIMA_GUN};

  public static WeaponMod getBubbleGun() {
    return BUBBLE_GUN;
  }

  public static WeaponMod getKittyCannon() {
    return KITTY_CANNON;
  }

  public static WeaponMod getSlimaGun() {
    return SLIMA_GUN;
  }

  private WeaponMods() {}

  @Contract("null -> null; !null -> _")
  public static @Nullable WeaponMod findByName(@Nullable String weaponModName) {
    for (WeaponMod weaponMod : REGISTRY) {
      if (weaponMod.mod().equals(weaponModName)) {
        return weaponMod;
      }
    }
    return null;
  }
}
