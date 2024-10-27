package agency.shitcoding.arena.models;

import agency.shitcoding.arena.SoundConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PowerupType {
  WEAPON(SoundConstants.WEAPON_PICKUP),
  AMMO(SoundConstants.AMMO_PICKUP),
  ARMOR(SoundConstants.BUFF_PICKUP),
  BUFF(SoundConstants.BUFF_PICKUP),
  MAJOR_BUFF(SoundConstants.PROTECT);

  private final String soundName;
}
