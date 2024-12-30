package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.gamestate.team.ETeam;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Flag {
  @Getter
  private final ETeam team;
  @Setter
  private FlagState state;
  @Setter
  private Player carrier;

  Flag(ETeam team) {
    this.team = team;
    this.state = FlagState.AT_BASE;
  }

  public boolean isAtBase() {
    return state == FlagState.AT_BASE;
  }
  public boolean isDropped() {
    return state == FlagState.DROPPED;
  }
  public boolean isCarried() {
    return state == FlagState.CARRIED;
  }
  public Optional<Player> getCarrier() {
    return Optional.ofNullable(carrier);
  }
  enum FlagState {
    AT_BASE,
    DROPPED,
    CARRIED
  }
}
