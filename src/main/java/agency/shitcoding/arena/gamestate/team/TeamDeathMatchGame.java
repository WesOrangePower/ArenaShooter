package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import org.bukkit.entity.Player;

public class TeamDeathMatchGame extends TeamGame {

  protected TeamDeathMatchGame(Arena arena, RuleSet ruleSet) {
    super(arena, ruleSet);
  }

  @Override
  public void updateScore(Player p, int delta) {
    var optTeam = teamManager.getTeam(p);
    if (optTeam.isEmpty()) {
      return;
    }
    var team = optTeam.get();
    var score = teamManager.getScore(team);
    teamManager.setScore(team, Math.max(score + delta, 0));
    updateScoreBoard();
  }
}
