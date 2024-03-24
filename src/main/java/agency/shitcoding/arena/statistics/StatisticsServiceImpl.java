package agency.shitcoding.arena.statistics;

import agency.shitcoding.arena.ArenaShooter;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatisticsServiceImpl implements
    StatisticsService {

  private static final Logger logger = ArenaShooter.getInstance().getLogger();

  private final File file;

  private boolean disabled;

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public StatisticsServiceImpl(File file) {
    this.file = file;
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      try {
        file.createNewFile();
      } catch (IOException e) {
        disabled = true;
        logger.log(Level.WARNING, "Failed to set up StatisticsService.", e);
      }
    }
  }

  @Override
  public void endGame(GameOutcome[] gameOutcomes) {
    if (disabled || gameOutcomes == null || gameOutcomes.length == 0) return;

    var charSink = Files.asCharSink(file, StandardCharsets.UTF_8, FileWriteMode.APPEND);
    for (GameOutcome gameOutcome : gameOutcomes) {
      try {
        charSink.write(gameOutcome.toString());
      } catch (IOException e) {
        logger.log(Level.WARNING, "Failed to write game outcome to stats file.", e);
      }
    }
  }

  @Override
  public Statistics getStatistics(String playerName) {
    if (disabled) {
      return new Statistics(playerName, 0, 0, 0, 0, 0);
    }

    try {
      var lines = Files.readLines(file, StandardCharsets.UTF_8);
      var statistics = new Statistics();
      lines.stream()
          .map(GameOutcome::fromString)
          .filter(gameOutcome -> gameOutcome.playerName().equals(playerName))
          .forEach(outcome -> {
            statistics.totalKills += outcome.kills();
            statistics.totalDeaths += outcome.deaths();
            statistics.matchesWon += outcome.isWon() ? 1 : 0;
            statistics.totalGames++;
          });

      statistics.playerName = playerName;
      statistics.killDeathRatio = statistics.totalDeaths == 0
          ? statistics.totalKills
          : (float) statistics.totalKills / statistics.totalDeaths;

      return statistics;
    } catch (IOException e) {
      logger.log(Level.WARNING, "Failed to read stats file.", e);
      return new Statistics(playerName, 0, 0, 0, 0, 0);
    }
  }

  @Override
  public List<GameOutcome> getGameOutcomes(Player player) {
    if (disabled) {
      return List.of();
    }

    try {
      var lines = Files.readLines(file, StandardCharsets.UTF_8);
      return lines.stream()
          .map(GameOutcome::fromString)
          .filter(outcome -> outcome.playerName().equals(player.getName()))
          .toList();
    } catch (IOException e) {
      logger.log(Level.WARNING, "Failed to read stats file.", e);
    }
    return List.of();
  }
}
