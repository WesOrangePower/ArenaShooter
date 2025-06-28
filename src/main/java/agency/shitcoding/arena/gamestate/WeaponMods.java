package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Weapon;

public class WeaponMods {
  private static final WeaponMod BUBBLE_GUN = new WeaponMod(Weapon.RAILGUN, "bubble_gun");
  private static final WeaponMod KITTY_CANNON = new WeaponMod(Weapon.ROCKET_LAUNCHER, "kitty_cannon");
  private static final WeaponMod SLIMA_GUN = new WeaponMod(Weapon.PLASMA_GUN, "slima_gun");

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
}
