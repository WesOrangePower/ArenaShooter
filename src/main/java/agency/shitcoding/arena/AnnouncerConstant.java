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
  TWO("two");

  private final String soundName;

  AnnouncerConstant(String soundName) {
    this.soundName = soundName;
  }
}
