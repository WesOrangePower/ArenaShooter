package agency.shitcoding.arena.gamestate.team;

import org.bukkit.scoreboard.Team;

public class BlueTeam extends GameTeam {

  public BlueTeam(Team scoreboardTeam) {
    super(scoreboardTeam, new BlueTeamMeta());
  }

  @Override
  public ETeam getETeam() {
    return ETeam.BLUE;
  }
}
