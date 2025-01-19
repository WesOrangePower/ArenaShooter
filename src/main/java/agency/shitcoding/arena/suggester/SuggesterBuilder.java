package agency.shitcoding.arena.suggester;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class SuggesterBuilder {

  private final List<SuggestionRule> rules;

  private SuggesterBuilder() {
    rules = new ArrayList<>();
  }

  public static SuggesterBuilder builder() {
    return new SuggesterBuilder();
  }

  public SuggesterRuleBuilder when(BiPredicate<CommandSender, String[]> condition) {
    return new SuggesterRuleBuilder(condition, this);
  }

  public SuggesterRuleBuilder at(int index) {
    return new SuggesterRuleBuilder((sender, args) -> args.length == index, this);
  }

  public SuggesterRuleBuilder rule() {
    return new SuggesterRuleBuilder((sender, args) -> true, this);
  }

  // package-private
  void addRule(SuggestionRule rule) {
    rules.add(rule);
  }

  public SuggesterBuilder join(SuggesterBuilder other) {
    rules.addAll(other.rules);
    return this;
  }

  public SuggesterBuilder join(Function<SuggesterBuilder, SuggesterBuilder> contextBasedOther) {
    return contextBasedOther.apply(this);
  }

  public SuggesterBuilder addRules(List<SuggestionRule> rules) {
    this.rules.addAll(rules);
    return this;
  }

  public Suggester build() {
    return new SuggesterImpl(rules);
  }

}
