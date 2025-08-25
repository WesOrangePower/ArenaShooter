package agency.shitcoding.arena.statistics;

import agency.shitcoding.arena.ArenaShooter;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public class StatisticsServiceImpl implements StatisticsService {

  private static final Logger logger = ArenaShooter.getInstance().getLogger();

  private final File file;

  private boolean disabled;

  private boolean isDataFresh = false;

  private GameOutcome @Nullable [] outcomes = null;

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public StatisticsServiceImpl(File file) {
    this.file = file;
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      try {
        if (file.createNewFile()) {
          logger.log(Level.INFO, "Stats file created");
        }
      } catch (IOException e) {
        disabled = true;
        logger.log(Level.WARNING, "Failed to set up StatisticsService.", e);
      }
    }
  }

  @Override
  public void endGame(GameOutcome @Nullable [] gameOutcomes) {
    if (disabled || gameOutcomes == null || gameOutcomes.length == 0) return;

    var charSink = Files.asCharSink(file, StandardCharsets.UTF_8, FileWriteMode.APPEND);
    for (GameOutcome gameOutcome : gameOutcomes) {
      try {
        charSink.write(gameOutcome.toString());
      } catch (IOException e) {
        logger.log(Level.WARNING, "Failed to write game outcome to stats file.", e);
      }
    }

    this.isDataFresh = false;
  }

  @Override
  public Statistics getStatistics(String playerName) {
    if (disabled) {
      return new Statistics(playerName, 0, 0, 0, 0, 0);
    }

    load();

    var statistics = new Statistics();
    assert outcomes != null;
    for (var outcome : outcomes) {
      if (outcome.playerName().equals(playerName)) {
        statistics.totalKills += outcome.kills();
        statistics.totalDeaths += outcome.deaths();
        statistics.matchesWon += outcome.isWon() ? 1 : 0;
        statistics.totalGames++;
      }
    }

    statistics.playerName = playerName;
    statistics.killDeathRatio =
        statistics.totalDeaths == 0
            ? statistics.totalKills
            : (float) statistics.totalKills / statistics.totalDeaths;

    return statistics;
  }

  @Override
  public List<GameOutcome> getGameOutcomes(Player player) {
    if (disabled) {
      return List.of();
    }

    load();

    var result = new ArrayList<GameOutcome>();

    var playerName = player.getName();
    assert outcomes != null;
    for (var outcome : outcomes) {
      if (outcome.playerName().equals(playerName)) {
        result.add(outcome);
      }
    }
    return result;
  }

  @Override
  public PriorityQueue<Statistics> getLeaderboard(LeaderBoardCriterion criteria) {
    if (disabled) {
      return new PriorityQueue<>();
    }

    load();

    var leaderboard = new PriorityQueue<>(criteria.comparator);
    assert outcomes != null;
    var playerNames =
        Stream.of(outcomes).map(GameOutcome::playerName).distinct().toArray(String[]::new);

    for (var playerName : playerNames) {
      var statistics = getStatistics(playerName);
      leaderboard.add(statistics);
    }

    return leaderboard;
  }

  private void load() {
    if (isDataFresh) return;

    try {
      var lines = Files.readLines(file, StandardCharsets.UTF_8);
      this.outcomes = lines.stream().map(GameOutcome::fromString).toArray(GameOutcome[]::new);
      this.isDataFresh = true;
    } catch (IOException e) {
      logger.log(Level.WARNING, "Failed to read stats file.", e);
    }
  }
}
