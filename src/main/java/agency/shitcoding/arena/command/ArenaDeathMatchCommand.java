package agency.shitcoding.arena.command;

import agency.shitcoding.arena.command.subcommands.*;
import agency.shitcoding.arena.gui.ArenaMainMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ArenaDeathMatchCommand extends CommandInst {

  public static final String ADMIN_PERM = "jelly.arena.admin";
  private static final HelpEntry[] HELP = new HelpEntry[]{
      new HelpEntry("join", "Join an arena"),
      new HelpEntry("host", "Host a game"),
      new HelpEntry("leave", "Leave an arena")
  };
  private static final HelpEntry[] HELP_ADMIN = new HelpEntry[]{
      new HelpEntry("set", "Set the arena's spawn points"),
      new HelpEntry("create", "Create a new arena"),
      new HelpEntry("join", "Join an arena"),
      new HelpEntry("leave", "Leave an arena"),
      new HelpEntry("forcestart", "Force start a game")
  };

  public ArenaDeathMatchCommand(@NotNull CommandSender sender,
      @NotNull String[] args) {
    super(sender, args);
  }

  public static String getAdminPerm() {
    return ADMIN_PERM;
  }

  @Override
  public void execute() {
    if (args.length == 0) {
      if (sender instanceof Player p) {
        new ArenaMainMenu(p).render();
        return;
      }
      if (sender.hasPermission(ADMIN_PERM)) {
        new ArenaHelpCmd(sender, args, HELP_ADMIN)
            .execute();
      } else {
        new ArenaHelpCmd(sender, args, HELP)
            .execute();
      }
      return;
    }
    CommandInst command = switch (args[0].toLowerCase()) {
      case "set" -> new ArenaSetCmd(sender, args);
      case "create" -> new ArenaCreateCmd(sender, args);
      case "host" -> new ArenaHostCmd(sender, args);
      case "join" -> new ArenaJoinCmd(sender, args);
      case "forcestart" -> new ArenaForcestartCmd(sender, args);
      case "leave" -> new ArenaLeaveCmd(sender, args);
      case "test" -> new ArenaTestCmd(sender, args);
      default -> new ArenaHelpCmd(sender, args, HELP);
    };
    command.execute();
  }
}

