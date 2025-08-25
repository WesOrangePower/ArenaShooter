package agency.shitcoding.arena.command;

import agency.shitcoding.arena.suggester.ArenaTabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;
import org.jspecify.annotations.Nullable;

public final class ArenaCommandInvoker extends Command implements TabCompleter, CommandExecutor {

  private static @Nullable ArenaCommandInvoker instance;

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
      CommandSender sender, String commandLabel, String[] args) {
    new ArenaCommand(sender, args).execute();
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(
      CommandSender sender,
      Command command,
      String label,
      String[] args) {

    return new ArenaTabCompleter(sender, args).onTabComplete();
  }

  @Override
  public boolean onCommand(
      CommandSender sender,
      Command command,
      String label,
      String[] args) {
    new ArenaCommand(sender, args).execute();
    return true;
  }
}
