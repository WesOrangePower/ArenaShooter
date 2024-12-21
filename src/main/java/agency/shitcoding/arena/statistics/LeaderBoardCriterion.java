package agency.shitcoding.arena.statistics;

import java.util.Comparator;
import java.util.function.Function;

public enum LeaderBoardCriterion {
  KDR(
      Comparator.comparing(s -> s.killDeathRatio, Comparator.reverseOrder()),
      s -> (double) s.killDeathRatio),
  KILLS(
      Comparator.comparing(s -> s.totalKills, Comparator.reverseOrder()),
      s -> (double) s.totalKills),
  WINS(
      Comparator.comparing(s -> s.matchesWon, Comparator.reverseOrder()),
      s -> (double) s.matchesWon),
  MATCHES(
      Comparator.comparing(s -> s.totalGames, Comparator.reverseOrder()),
      s -> (double) s.totalGames);

  LeaderBoardCriterion(Comparator<Statistics> comparator, Function<Statistics, Double> selector) {
    this.comparator = comparator;
    this.selector = selector;
  }

  public final Comparator<Statistics> comparator;
  public final Function<Statistics, Double> selector;
}
