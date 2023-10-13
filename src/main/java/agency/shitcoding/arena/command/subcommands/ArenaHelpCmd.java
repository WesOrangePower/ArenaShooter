package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.command.HelpEntry;
import agency.shitcoding.arena.gui.ArenaMainMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
        if (!(sender instanceof Player p)) {
            return;
        }
        new ArenaMainMenu(p).render();
    }

}
