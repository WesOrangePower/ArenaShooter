package agency.shitcoding.arena.command;

import agency.shitcoding.arena.ArenaDeathMatchTabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArenaDeathMatchCommandInvoker extends Command implements TabCompleter,
    CommandExecutor {

  public static ArenaDeathMatchCommandInvoker INSTANCE = null;

  private ArenaDeathMatchCommandInvoker() {
    super("arenaDeathMatch".toLowerCase());
  }

  public static ArenaDeathMatchCommandInvoker getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ArenaDeathMatchCommandInvoker();
    }
    return INSTANCE;
  }

  @Override
  public boolean
  execute(@NotNull CommandSender sender,
      @NotNull String commandLabel,
      @NotNull String[] args) {
    new ArenaDeathMatchCommand(sender, args)
        .execute();
    return true;
  }

  @Override
  public @Nullable List<String>
  onTabComplete(@NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {

    return new ArenaDeathMatchTabCompleter(sender, args)
        .onTabComplete();
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {
    new ArenaDeathMatchCommand(sender, args)
        .execute();
    return true;
  }
}
