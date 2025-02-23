package agency.shitcoding.arena.command;

import agency.shitcoding.arena.suggester.ArenaTabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ArenaCommandInvoker extends Command implements TabCompleter, CommandExecutor {

  private static ArenaCommandInvoker instance;

  private ArenaCommandInvoker() {
    super("arena".toLowerCase());
  }

  public static ArenaCommandInvoker getInstance() {
    if (instance == null) {
      instance = new ArenaCommandInvoker();
    }
    return instance;
  }

  @Override
  public boolean execute(
      @NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
    new ArenaCommand(sender, args).execute();
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {

    return new ArenaTabCompleter(sender, args).onTabComplete();
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {
    new ArenaCommand(sender, args).execute();
    return true;
  }
}
