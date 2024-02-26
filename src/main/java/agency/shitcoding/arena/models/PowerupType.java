package agency.shitcoding.arena.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PowerupType {
  WEAPON("jelly:q3.weapon.we_pick"),
  AMMO("jelly:q3.weapon.am_pick"),
  ARMOR("jelly:q3.weapon.bu_pick"),
  BUFF("jelly:q3.weapon.bu_pick"),
  MAJOR_BUFF("jelly:q3.weapon.protect");

  private final String soundName;
}
