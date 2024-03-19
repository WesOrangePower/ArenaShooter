package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.GameStage;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.statistics.GameOutcome;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class LMSGame extends DeathMatchGame {

  private Player winner = null;

  public LMSGame(Arena arena) {
    super(arena, RuleSet.LMS);
  }


  @Override
  public void onPlayerDeath(Player player) {
    super.onPlayerDeath(player);
    if (getGamestage() == GameStage.IN_PROGRESS) {
      checkLms();
    }
  }

  public void checkLms() {
    Set<Player> playersThatNeverDied = getPlayers().stream()
        .filter(p -> !getDiedOnce().contains(p))
        .collect(Collectors.toSet());

    if (playersThatNeverDied.size() == 1) {
      Player player = playersThatNeverDied.stream().findFirst().orElseThrow();
      this.winner = player;
      endGame("game.end.lms", true, player.getName());
    }
  }

  @Override
  protected GameOutcome[] getGameOutcomes() {
    return scores.stream()
        .map(score -> new GameOutcome(
            score.getPlayer().getName(),
            getRuleSet(),
            getStatKills().getOrDefault(score.getPlayer(), 0),
            getStatDeaths().getOrDefault(score.getPlayer(), 0),
            score.getScore(),
            winner != null && winner.getName().equals(score.getPlayer().getName()),
            Instant.now(),
            arena.getName()
        ))
        .toArray(GameOutcome[]::new);
  }
}
