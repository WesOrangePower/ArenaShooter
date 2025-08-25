package agency.shitcoding.arena.command;

import agency.shitcoding.arena.command.subcommands.ArenaLeaveCmd;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jspecify.annotations.Nullable;

public final class LeaveCommandInvoker implements TabCompleter, CommandExecutor {
  private static @Nullable LeaveCommandInvoker instance = null;

  public static LeaveCommandInvoker getInstance() {
    if (instance == null) {
      instance = new LeaveCommandInvoker();
    }
    return instance;
  }

  @Override
  public boolean onCommand(
      CommandSender sender,
      Command command,
      String label,
      String[] args) {
    new ArenaLeaveCmd(sender, args).execute();
    return true;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender,
      Command command,
      String label,
      String[] args) {
    return List.of();
  }
}
