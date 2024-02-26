package agency.shitcoding.arena.gamestate.team;

import org.bukkit.scoreboard.Team;

public class RedTeam extends GameTeam {
  public RedTeam(Team scoreboardTeam) {
    super(scoreboardTeam, new RedTeamMeta());
  }

  @Override
  public ETeam getETeam() {
    return ETeam.RED;
  }
}
