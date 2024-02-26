package agency.shitcoding.arena.gamestate.team;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TeamScore {
  private final GameTeam team;
  private int score;
}
