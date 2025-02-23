package agency.shitcoding.arena.command;

import agency.shitcoding.arena.command.subcommands.ArenaLeaveCmd;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

public final class LeaveCommandInvoker implements TabCompleter, CommandExecutor {
  private static LeaveCommandInvoker instance = null;

  public static LeaveCommandInvoker getInstance() {
    if (instance == null) {
      instance = new LeaveCommandInvoker();
    }
    return instance;
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {
    new ArenaLeaveCmd(sender, args).execute();
    return true;
  }

  @Override
  public List<String> onTabComplete(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {
    return List.of();
  }
}
