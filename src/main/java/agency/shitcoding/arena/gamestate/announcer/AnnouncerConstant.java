package agency.shitcoding.arena.gamestate.announcer;

import agency.shitcoding.arena.localization.SupportedLocale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NonNls;

import static agency.shitcoding.arena.localization.SupportedLocale.ALL;
import static agency.shitcoding.arena.localization.SupportedLocale.EN;

@SuppressWarnings("SpellCheckingInspection")
@Getter
@RequiredArgsConstructor
public enum AnnouncerConstant {
  ONE_MINUTE("1_minute", ALL),
  FIVE_MINUTE("5_minute", ALL),
  EXCELLENT("excellent", ALL),
  FIGHT("fight", ALL),
  HOLYSHIT("holyshit", ALL),
  HUMILIATION("humiliation", ALL),
  IMPRESSIVE("impressive", ALL),
  LOSTLEAD("lostlead", ALL),
  ONE("one", ALL),
  TAKEN_LEAD("taken_lead", ALL),
  THREE("three", ALL),
  TIED_LEAD("tied_lead", ALL),
  TWO("two", ALL),
  YOUR_FLAG_RETURNED("your_flag_returned", ALL),
  ENEMY_FLAG_RETURNED("enemy_flag_returned", ALL),
  YOUR_FLAG_TAKEN("your_flag_taken", ALL),
  ENEMY_FLAG_TAKEN("enemy_flag_taken", ALL),
  YOUR_TEAM_SCORED("your_team_scored", ALL),
  ENEMY_TEAM_SCORED("enemy_team_scored", ALL),
  TUTORIAL_INTRODUCTION("tutorial_introduction", new SupportedLocale[]{EN}),
  TUTORIAL_MOVEMENT_1("tutorial_movement_1", new SupportedLocale[]{EN}),
  TUTORIAL_MOVEMENT_2("tutorial_movement_2", new SupportedLocale[]{EN}),
  TUTORIAL_AMMO_1("tutorial_ammo_1", new SupportedLocale[]{EN}),
  TUTORIAL_AMMO_2("tutorial_ammo_2", new SupportedLocale[]{EN}),
  TUTORIAL_WEAPON_GAUNTLET("tutorial_weapon_gauntlet", new SupportedLocale[]{EN}),
  TUTORIAL_WEAPON_MACHINEGUN("tutorial_weapon_machinegun", new SupportedLocale[]{EN}),
  TUTORIAL_WEAPON_SHOTGUN("tutorial_weapon_shotgun", new SupportedLocale[]{EN}),
  TUTORIAL_WEAPON_ROCKETLAUNCHER("tutorial_weapon_rocketlauncher", new SupportedLocale[]{EN}),
  TUTORIAL_RULESET_ROF("tutorial_ruleset_rof", new SupportedLocale[]{EN}),
  TUTORIAL_WEAPON_PLASMAGUN("tutorial_weapon_plasmagun", new SupportedLocale[]{EN}),
  TUTORIAL_WEAPON_LIGHTNINGGUN("tutorial_weapon_lightninggun", new SupportedLocale[]{EN}),
  TUTORIAL_WEAPON_RAILGUN("tutorial_weapon_railgun", new SupportedLocale[]{EN}),
  TUTORIAL_RULESET_INSTAGIB("tutorial_ruleset_instagib", new SupportedLocale[]{EN}),
  TUTORIAL_WEAPON_BFG9K("tutorial_weapon_bfg9k", new SupportedLocale[]{EN}),
  TUTORIAL_WEAPON_1("tutorial_weapon_1", new SupportedLocale[]{EN}),
  TUTORIAL_ROCKETJUMPING_1("tutorial_rocketjumping_1", new SupportedLocale[]{EN}),
  TUTORIAL_POWERUPS_1("tutorial_powerups_1", new SupportedLocale[]{EN}),
  TUTORIAL_POWERUPS_2("tutorial_powerups_2", new SupportedLocale[]{EN}),
  TUTORIAL_POWERUPS_3("tutorial_powerups_3", new SupportedLocale[]{EN}),
  TUTORIAL_CONCLUSION("tutorial_conclusion", new SupportedLocale[]{EN}),
  TUTORIAL_PRIZE("tutorial_prize", new SupportedLocale[]{EN}),
  TUTORIAL_GOODLUCK("tutorial_goodluck", new SupportedLocale[]{EN});


  private final String soundName;
  private final SupportedLocale[] supportedLocales;

  @Setter private static AnnouncementSkipProvider announcementSkipProvider = null;

  public boolean supports(SupportedLocale locale) {
    for (SupportedLocale supportedLocale : supportedLocales) {
      if (supportedLocale == locale) {
        return true;
      }
    }
    return false;
  }

  public AnnouncementSkip getSkip() {
    return announcementSkipProvider.getAnnouncementSkip(this);
  }
}
