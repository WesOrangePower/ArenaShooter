package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.LootPoint;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.statistics.GameOutcome;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TeamDeathMatchGame extends TeamGame {

  protected TeamDeathMatchGame(Arena arena, RuleSet ruleSet) {
    super(arena, ruleSet);
  }

  @Override
  protected @NotNull Set<LootPoint> preprocessLootPoints(Set<LootPoint> lootPoints) {
    return lootPoints;
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
