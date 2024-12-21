package agency.shitcoding.arena.statistics;

import java.util.Collection;
import java.util.PriorityQueue;

import org.bukkit.entity.Player;

public interface StatisticsService {
  void endGame(GameOutcome[] gameOutcomes);
  Statistics getStatistics(String playerName);

  default Statistics getStatistics(Player player) {
    return getStatistics(player.getName());
  }

  Collection<GameOutcome> getGameOutcomes(Player player);

  PriorityQueue<Statistics> getLeaderboard(LeaderBoardCriterion criteria);
}

