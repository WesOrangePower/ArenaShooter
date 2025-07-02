package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Weapon;

public record WeaponMod(Weapon weapon, String mod)
{
  public String getTranslationKey() {
    return weapon.translatableName + ".mod." + mod;
  }
}
