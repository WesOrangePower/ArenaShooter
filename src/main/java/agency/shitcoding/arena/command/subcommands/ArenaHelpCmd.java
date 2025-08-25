package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.command.HelpEntry;
import org.bukkit.command.CommandSender;

public class ArenaHelpCmd extends CommandInst {

  private final HelpEntry[] helpEntries;

  public ArenaHelpCmd(CommandSender sender, String[] args,
      HelpEntry[] helpEntries) {
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
