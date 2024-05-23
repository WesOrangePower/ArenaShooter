package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Weapon;

public class WeaponMods {
    private WeaponMods() {
    }

    private static final WeaponMod BUBBLE_GUN = new WeaponMod(Weapon.RAILGUN, "bubbleGun");
    private static final WeaponMod KITTY_CANNON = new WeaponMod(Weapon.ROCKET_LAUNCHER, "kittyCannon");

    public static WeaponMod getBubbleGun() {
        return BUBBLE_GUN;
    }

    public static WeaponMod getKittyCannon() {
        return KITTY_CANNON;
    }
}
