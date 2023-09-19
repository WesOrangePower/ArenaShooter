package agency.shitcoding.doublejump.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class ArenaDeathMatchCommand extends CommandInst {
    public static final String ADMIN_PERM = "jelly.arena.admin";
    public ArenaDeathMatchCommand(@NotNull CommandSender sender,
                                  @NotNull String[] args) {
        super(sender, args);
    }

    @Override
    public void execute() {
        if (args.length == 0) {
            if (sender.hasPermission(ADMIN_PERM)) {
                sender.sendRichMessage(
                        "<#d8542f>Usage: /arena [<#D8A82F>set</#D8A82F><#B4D82F> | </#B4D82F><#D8A82F>create</#D8A82F><#B4D82F> | </#B4D82F><#D8A82F>join</#D8A82F><#B4D82F> | </#B4D82F><#D8A82F>leave</#D8A82F><#d8542f>]"
                );
                return;
            }
            sender.sendRichMessage(
                    "<#d8542f>Usage: /arena [<#D8A82F>join</#D8A82F><#B4D82F> | </#B4D82F><#D8A82F>leave</#D8A82F><#d8542f>]"
            );
        }
    }
}
