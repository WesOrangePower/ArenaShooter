package agency.shitcoding.arena.gamestate.announcer;

import java.util.Map;

import static agency.shitcoding.arena.gamestate.announcer.AnnouncerConstant.*;
import static agency.shitcoding.arena.localization.SupportedLocale.*;

public class HardcodedStaticAnnouncementSkipProvider implements AnnouncementSkipProvider {
  public static Map<AnnouncerConstant, AnnouncementSkip> constantSkipMap =
      Map.ofEntries(
          Map.entry(
              ONE_MINUTE,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1298)
                  .fromMillies(ET, 1309)
                  .fromMillies(PT, 1718)
                  .fromMillies(RU, 1326)
                  .fromMillies(UK, 1927)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              FIVE_MINUTE,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1462)
                  .fromMillies(ET, 1541)
                  .fromMillies(PT, 2025)
                  .fromMillies(RU, 1230)
                  .fromMillies(UK, 1625)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              EXCELLENT,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 823)
                  .fromMillies(ET, 1042)
                  .fromMillies(PT, 1173)
                  .fromMillies(RU, 969)
                  .fromMillies(UK, 975)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              FIGHT,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 757)
                  .fromMillies(ET, 878)
                  .fromMillies(PT, 684)
                  .fromMillies(RU, 651)
                  .fromMillies(UK, 627)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              HOLYSHIT,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1219)
                  .fromMillies(ET, 1085)
                  .fromMillies(PT, 987)
                  .fromMillies(RU, 709)
                  .fromMillies(UK, 952)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              HUMILIATION,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1212)
                  .fromMillies(ET, 1068)
                  .fromMillies(PT, 1138)
                  .fromMillies(RU, 1201)
                  .fromMillies(UK, 1080)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              IMPRESSIVE,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 929)
                  .fromMillies(ET, 1162)
                  .fromMillies(PT, 1358)
                  .fromMillies(RU, 1046)
                  .fromMillies(UK, 906)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              LOSTLEAD,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 955)
                  .fromMillies(ET, 1326)
                  .fromMillies(PT, 1683)
                  .fromMillies(RU, 1302)
                  .fromMillies(UK, 1405)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              ONE,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 586)
                  .fromMillies(ET, 809)
                  .fromMillies(PT, 731)
                  .fromMillies(RU, 670)
                  .fromMillies(UK, 825)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              TAKEN_LEAD,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1041)
                  .fromMillies(ET, 1490)
                  .fromMillies(PT, 1916)
                  .fromMillies(RU, 1162)
                  .fromMillies(UK, 1253)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              THREE,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 889)
                  .fromMillies(ET, 697)
                  .fromMillies(PT, 697)
                  .fromMillies(RU, 448)
                  .fromMillies(UK, 569)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              TIED_LEAD,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1436)
                  .fromMillies(ET, 1033)
                  .fromMillies(PT, 2450)
                  .fromMillies(RU, 1442)
                  .fromMillies(UK, 1637)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              TWO,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 889)
                  .fromMillies(ET, 698)
                  .fromMillies(PT, 613)
                  .fromMillies(RU, 661)
                  .fromMillies(UK, 662)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              YOUR_FLAG_RETURNED,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1447)
                  .fromMillies(ET, 1490)
                  .fromMillies(PT, 2221)
                  .fromMillies(RU, 1670)
                  .fromMillies(UK, 1660)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              ENEMY_FLAG_RETURNED,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1536)
                  .fromMillies(ET, 1679)
                  .fromMillies(PT, 1840)
                  .fromMillies(RU, 1974)
                  .fromMillies(UK, 1625)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              YOUR_FLAG_TAKEN,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1389)
                  .fromMillies(ET, 1395)
                  .fromMillies(PT, 1865)
                  .fromMillies(RU, 1582)
                  .fromMillies(UK, 1625)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              ENEMY_FLAG_TAKEN,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1488)
                  .fromMillies(ET, 1627)
                  .fromMillies(PT, 2215)
                  .fromMillies(RU, 1757)
                  .fromMillies(UK, 1579)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              YOUR_TEAM_SCORED,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1537)
                  .fromMillies(ET, 1481)
                  .fromMillies(PT, 1481)
                  .fromMillies(RU, 1713)
                  .fromMillies(UK, 1539)
                  .addGraceTimeToAll()
                  .build()),
          Map.entry(
              ENEMY_TEAM_SCORED,
              ConstantAnnouncementSkip.builder()
                  .fromMillies(EN, 1644)
                  .fromMillies(ET, 1739)
                  .fromMillies(PT, 1675)
                  .fromMillies(RU, 1989)
                  .fromMillies(UK, 1547)
                  .addGraceTimeToAll()
                  .build()));

  @Override
  public AnnouncementSkip getAnnouncementSkip(AnnouncerConstant announcerConstant) {
    return constantSkipMap.get(announcerConstant);
  }
}
