package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.models.GameRules;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.worlds.ArenaWorld;
import org.bukkit.entity.Player;

public class TeamDeathMatchGame extends TeamGame {

  protected TeamDeathMatchGame(ArenaWorld arena, RuleSet ruleSet) {
    this(arena, ruleSet, ruleSet.getDefaultGameRules());
  }
  public TeamDeathMatchGame(ArenaWorld arena, RuleSet ruleSet, GameRules gameRules) {
    super(arena, ruleSet, gameRules);
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
