package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.gamestate.team.ETeam;
import java.util.EnumMap;

public class FlagManager {
  private final EnumMap<ETeam, Flag> flags = new EnumMap<>(ETeam.class);

  public FlagManager() {
    for (ETeam value : ETeam.values()) {
      flags.put(value, new Flag(value));
    }
  }

  public Flag getFlag(ETeam team) {
    return flags.get(team);
  }
}
