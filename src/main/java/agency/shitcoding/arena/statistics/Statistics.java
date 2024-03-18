package agency.shitcoding.arena.statistics;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Statistics {
  public String playerName;
  public int totalKills;
  public int totalDeaths;
  public float killDeathRatio;
  public int totalGames;
  public int matchesWon;
}
