package agency.shitcoding.arena.command;

import agency.shitcoding.arena.command.subcommands.*;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class ArenaDeathMatchCommand extends CommandInst {
    public static final String ADMIN_PERM = "jelly.arena.admin";
    public static final HelpEntry[] HELP = new HelpEntry[]{
            new HelpEntry("join", "Join an arena"),
            new HelpEntry("leave", "Leave an arena")
    };
    public static final HelpEntry[] HELP_ADMIN = new HelpEntry[]{
            new HelpEntry("set", "Set the arena's spawn points"),
            new HelpEntry("create", "Create a new arena"),
            new HelpEntry("join", "Join an arena"),
            new HelpEntry("leave", "Leave an arena")
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
            case "leave" -> new ArenaLeaveCmd(sender, args);
            default -> new ArenaHelpCmd(sender, args, HELP);
        };
        command.execute();
    }
}

