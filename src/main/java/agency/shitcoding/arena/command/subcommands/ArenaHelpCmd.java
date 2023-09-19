package agency.shitcoding.arena.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ArenaHelpCmd extends CommandInst {

    private final HelpEntry[] helpEntries;

    public ArenaHelpCmd(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HelpEntry[] helpEntries) {
        super(sender, args);
        this.helpEntries = helpEntries;
    }

    @Override
    public void execute() {
        for (HelpEntry entry : helpEntries) {
            sender.sendRichMessage(entry.getHelpMessage());
        }
    }

}
