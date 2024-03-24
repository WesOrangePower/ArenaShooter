package agency.shitcoding.arena.statistics;

import agency.shitcoding.arena.models.RuleSet;

import java.time.Instant;

public record GameOutcome(
    String playerName,
    RuleSet ruleSet,
    int kills,
    int deaths,
    int points,
    boolean isWon,
    Instant time,
    String map
) {

  @Override
  public String toString() {
    return String.format("%s,%s,%d,%d,%d,%b,%s,%s%n",
        playerName,
        ruleSet.name(),
        kills,
        deaths,
        points,
        isWon,
        time,
        map
    );
  }

  public static GameOutcome fromString(String line) {
    String[] parts = line.split(",");
    return new GameOutcome(
        parts[0],
        RuleSet.valueOf(parts[1]),
        Integer.parseInt(parts[2]),
        Integer.parseInt(parts[3]),
        Integer.parseInt(parts[4]),
        Boolean.parseBoolean(parts[5]),
        Instant.parse(parts[6]),
        parts[7]
    );
  }
}
