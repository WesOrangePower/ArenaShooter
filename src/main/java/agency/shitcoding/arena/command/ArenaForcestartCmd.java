package agency.shitcoding.arena.command;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.localization.LangPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ArenaForcestartCmd extends CommandInst {

  public static final int ARENA_ARG = 1;

  public ArenaForcestartCmd(@NotNull CommandSender sender, String[] args) {
    super(sender, args);
  }

  @Override
  public void execute() {
    if (validate()) {
      var arena = args[ARENA_ARG];
      GameOrchestrator.getInstance().getGameByArena(arena).ifPresent(Game::forceStart);
    }
  }

  private boolean validate() {
    if (!sender.hasPermission(ArenaDeathMatchCommand.ADMIN_PERM)) {
      return false;
    }
    if (args.length != 2) {
      sender.sendRichMessage("<dark_red>Use: /arenaDeathMatch forcestart <arena>");
      return false;
    }

    var arena = args[ARENA_ARG];
    if (!GameOrchestrator.getInstance().getUsedArenaNames().contains(arena)) {
      send("command.forceStart.notFound", "<dark_red> Arena not found: %s", arena);
      return false;
    }

    return true;
  }

  public void send(String message, String defaultMessage, Object... formatArgs) {
    if (sender instanceof Player p) {
      new LangPlayer(p).sendRichLocalized(message, formatArgs);
    } else {
      sender.sendMessage(defaultMessage);
    }
  }
}
