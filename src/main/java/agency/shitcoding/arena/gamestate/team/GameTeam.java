package agency.shitcoding.arena.gamestate.team;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public abstract class GameTeam {

  private final Team scoreboardTeam;
  private final Set<Player> players = new HashSet<>();
  private final TeamMeta teamMeta;

  protected GameTeam(Team scoreboardTeam, TeamMeta teamMeta) {
    this.scoreboardTeam = scoreboardTeam;
    this.teamMeta = teamMeta;

    scoreboardTeam.setAllowFriendlyFire(false);
    scoreboardTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
  }

  public abstract ETeam getETeam();

  public int count() {
    return getPlayers().size();
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj.getClass().equals(this.getClass());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getETeam());
  }
}
