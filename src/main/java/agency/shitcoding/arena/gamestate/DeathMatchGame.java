package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import java.util.Collections;
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
