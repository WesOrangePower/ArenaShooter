package agency.shitcoding.arena;

import lombok.Getter;

@Getter
public enum AnnouncerConstant {
  ONE_MINUTE("1_minute"),
  FIVE_MINUTE("5_minute"),
  EXCELLENT("excellent"),
  FIGHT("fight"),
  HOLYSHIT("holyshit"),
  HUMILIATION("humiliation"),
  IMPRESSIVE("impressive"),
  LOSTLEAD("lostlead"),
  ONE("one"),
  TAKEN_LEAD("taken_lead"),
  THREE("three"),
  TIED_LEAD("tied_lead"),
  TWO("two"),
  YOUR_FLAG_RETURNED("your_flag_returned"),
  ENEMY_FLAG_RETURNED("enemy_flag_returned"),
  YOUR_FLAG_TAKEN("your_flag_taken"),
  ENEMY_FLAG_TAKEN("enemy_flag_taken"),
  YOUR_TEAM_SCORED("your_team_scored"),
  ENEMY_TEAM_SCORED("enemy_team_scored");

  private final String soundName;

  AnnouncerConstant(String soundName) {
    this.soundName = soundName;
  }
}
