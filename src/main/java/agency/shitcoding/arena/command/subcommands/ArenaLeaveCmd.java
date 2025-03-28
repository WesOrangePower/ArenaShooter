package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.Lobby;
import agency.shitcoding.arena.localization.LangPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ArenaLeaveCmd extends CommandInst {

  public ArenaLeaveCmd(@NotNull CommandSender sender, @NotNull String[] args) {
    super(sender, args);
  }

  @Override
  public void execute() {
    if (sender instanceof Player player) {
      GameOrchestrator gameOrchestrator = GameOrchestrator.getInstance();
      Optional<Game> game = gameOrchestrator.getGameByPlayer(player);
      LangPlayer lang = new LangPlayer(player);
      if (game.isEmpty()) {
        sender.sendRichMessage(lang.getLocalized("command.leave.notInGame"));
        return;
      }
      game.get().removePlayer(player);
      sender.sendRichMessage(lang.getLocalized("command.leave.success"));
      Lobby.getInstance().sendPlayer(player);
      return;
    }
    sender.sendRichMessage("<dark_red> Only players can use this command");
  }

}
