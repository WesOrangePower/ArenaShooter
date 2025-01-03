package agency.shitcoding.arena.command.subcommands.arenamutation.processors;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.storage.StorageProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class RuleSetMutationProcessor extends AbstractProcessor {
  @Override
  public final void add(Arena arena, String value, CommandSender commandSender) {
    RuleSet ruleSet;
    try {
      ruleSet = RuleSet.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      commandSender.sendMessage(Component.text("RuleSet " + value + " does not exist"));
      return;
    }
    arena.getSupportedRuleSets().add(ruleSet);

    StorageProvider.getArenaStorage().storeArena(arena);
    commandSender.sendMessage(Component.text("RuleSet " + ruleSet.name() + " added"));
  }

  @Override
  public void remove(Arena arena, String value, CommandSender commandSender) {
    RuleSet ruleSet;
    try {
      ruleSet = RuleSet.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      commandSender.sendMessage(Component.text("RuleSet " + value + " does not exist"));
      return;
    }

    if (!arena.getSupportedRuleSets().contains(ruleSet)) {
      commandSender.sendMessage(Component.text("RuleSet " + ruleSet.name() + " not found"));
      return;
    }

    arena.getSupportedRuleSets().remove(ruleSet);

    StorageProvider.getArenaStorage().storeArena(arena);
    commandSender.sendMessage(Component.text("RuleSet " + ruleSet.name() + " removed"));
  }

  @Override
  public void get(Arena arena, String value, CommandSender commandSender) {
    arena.getSupportedRuleSets().stream()
        .map(RuleSet::name)
        .map(Component::text)
        .reduce((a, b) -> a.append(Component.text(", ")).append(b))
        .ifPresent(commandSender::sendMessage);
  }
}
