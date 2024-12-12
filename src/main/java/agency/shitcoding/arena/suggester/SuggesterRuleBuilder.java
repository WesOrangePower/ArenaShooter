package agency.shitcoding.arena.suggester;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class SuggesterRuleBuilder {

  private BiPredicate<CommandSender, String[]> condition;
  private final SuggesterBuilder parent;

  public SuggesterRuleBuilder(
      BiPredicate<CommandSender, String[]> condition, SuggesterBuilder parent) {
    this.condition = condition;
    this.parent = parent;
  }

  public SuggesterRuleBuilder inCase(BiPredicate<CommandSender, String[]> condition) {
    this.condition = this.condition.and(condition);
    return this;
  }

  public SuggesterRuleBuilder inCaseNot(BiPredicate<CommandSender, String[]> condition) {
    this.condition = this.condition.and(condition.negate());
    return this;
  }

  public SuggesterRuleBuilder inCaseArgIs(int argIndex, String value) {
    return inCase((sender, args) -> args.length > argIndex && args[argIndex].equals(value));
  }

  public SuggesterRuleBuilder inCaseArgIsIgnoreCase(int argIndex, String value) {
    return inCase(
        (sender, args) -> args.length > argIndex && args[argIndex].equalsIgnoreCase(value));
  }

  public SuggesterBuilder suggest(@NotNull Supplier<List<String>> suggestions) {
    parent.addRule(new SuggestionRule(condition, suggestions));
    return parent;
  }

  public SuggesterBuilder suggestInts(Supplier<IntStream> intStream) {
    return suggest(() -> intStream.get().mapToObj(String::valueOf).toList());
  }

  public SuggesterBuilder suggestPlayers() {
    return suggest(() -> null);
  }

  public <E extends Enum<?>> SuggesterBuilder suggestEnumLower(Supplier<Class<E>> enumClass) {
    return suggest(() ->
        Stream.of(enumClass.get().getEnumConstants())
            .map(Enum::name)
            .map(String::toLowerCase)
            .toList());
  }
}
