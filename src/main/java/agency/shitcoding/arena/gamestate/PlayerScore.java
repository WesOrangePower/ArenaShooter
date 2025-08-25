package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.PlayerStreak;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Data
@AllArgsConstructor
public class PlayerScore implements Comparable<PlayerScore> {

  private int score;
  private Player player;
  private PlayerStreak streak;

  @Override
  public int compareTo(PlayerScore o) {
    return Integer.compare(o.score, this.score); // Reversed
  }
}
