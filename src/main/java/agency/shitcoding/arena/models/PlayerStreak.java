package agency.shitcoding.arena.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStreak {

  private int consequentRailHit = 0;
  private int fragStreak = 0;
}
