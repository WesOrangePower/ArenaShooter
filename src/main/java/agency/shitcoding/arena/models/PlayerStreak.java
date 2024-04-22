package agency.shitcoding.arena.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStreak {

  private int consequentRailHit;
  private int fragStreak;

  public PlayerStreak copy() {
    return new PlayerStreak(consequentRailHit, fragStreak);
  }
}
