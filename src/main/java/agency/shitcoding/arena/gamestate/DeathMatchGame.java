package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.statistics.GameOutcome;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class DeathMatchGame extends Game {

  public DeathMatchGame(Arena arena) {
    this(arena, RuleSet.DM);
  }

  public DeathMatchGame(Arena arena, RuleSet ruleSet) {
    super(arena, ruleSet);
  }

  @Override
  protected GameOutcome[] getGameOutcomes() {
    Optional<PlayerScore> max = scores.stream()
        .max(Comparator.comparingInt(PlayerScore::getScore));
    return scores.stream()
        .map(score -> new GameOutcome(
            score.getPlayer().getName(),
            getRuleSet(),
            getStatKills().getOrDefault(score.getPlayer(), 0),
            getStatDeaths().getOrDefault(score.getPlayer(), 0),
            score.getScore(),
            max.map(playerScore -> playerScore.getPlayer().getName().equals(score.getPlayer().getName()))
                .orElse(false),
            Instant.now(),
            arena.getName()
        ))
        .toArray(GameOutcome[]::new);
  }

  @Override
  protected Component getGameStatComponent() {
    MiniMessage mm = MiniMessage.miniMessage();
    Builder builder = Component.text();
    Collections.sort(scores);
    for (PlayerScore score : scores) {
      String message = String.format("<green><bold>%s<gold>: <red>%d",
          score.getPlayer().getName(),
          score.getScore()
      );
      builder.appendNewline().append(mm.deserialize(message));
    }
    return builder.build();
  }

}
