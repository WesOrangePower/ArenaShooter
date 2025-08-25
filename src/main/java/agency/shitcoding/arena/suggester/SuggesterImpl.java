package agency.shitcoding.arena.suggester;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.Nullable;

public class SuggesterImpl implements Suggester {

  private final List<SuggestionRule> rules;
  
  public SuggesterImpl(List<SuggestionRule> rules) {
    this.rules = rules;
  }

  @Override
  public @Nullable List<String> suggest(CommandSender sender, String[] args) {
    for (SuggestionRule rule : rules) {
      if (rule.condition().test(sender, args)) {
        return rule.suggestions().get();
      }
    }
    return Collections.emptyList();
  }

  @Override
  public Suggester combine(Suggester other) {
    rules.addAll(((SuggesterImpl) other).rules);
    return this;
  }

}

