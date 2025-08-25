package agency.shitcoding.arena.command;

import static io.vavr.control.Validation.invalid;
import static io.vavr.control.Validation.valid;
import static java.util.Objects.requireNonNull;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.localization.LangContext;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.localization.SupportedLocale;
import agency.shitcoding.arena.models.GameStage;
import io.vavr.control.Validation;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class ArenaForceStartCmd extends CommandInst {

  public static final int PLAYER_NAME_ARG = 1;

  private @Nullable Game foundGame = null;

  public ArenaForceStartCmd(CommandSender sender, String[] args) {
    super(sender, args);
  }

  @Override
  public void execute() {
    if (validateAndSend()) {
      assert foundGame != null;
      foundGame.forceStart();
      send("command.forceStart.success");
    }
  }

  private boolean validateAndSend() {
    var validation = validate();
    if (validation.isInvalid()) {
      send(validation.getError());
      return false;
    }
    return true;
  }

  private Validation<String, Game> validate() {
    if (!sender.hasPermission(ArenaCommand.ADMIN_PERM)) {
      return invalid("command.noPermission");
    }
    if (args.length != 2) {
      if (sender instanceof Player player) {
        GameOrchestrator.getInstance().getGameByPlayer(player)
            .ifPresent(game -> this.foundGame = game);
      }
      if (foundGame != null) {
        return valid(foundGame);
      }
      return invalid("command.forceStart.usage");
    }

    var playerName = args[PLAYER_NAME_ARG];
    var player = requireNonNull(Bukkit.getPlayer(playerName));
    var game = GameOrchestrator.getInstance().getGameByPlayer(player);
    if (game.isEmpty()) {
      return invalid("command.forceStart.notInGame");
    }
    this.foundGame = game.get();
    if (foundGame.getGamestage() != GameStage.WAITING) {
      return invalid("command.forceStart.notWaiting");
    }
    return valid(foundGame);
  }

  public void send(String message, Object... formatArgs) {
    if (sender instanceof Player p) {
      new LangPlayer(p).sendRichLocalized(message, formatArgs);
    } else {
      var defaultCtx = new LangContext(SupportedLocale.EN);
      var localized = defaultCtx.getLocalized(message, formatArgs);
      sender.sendRichMessage(localized);
    }
  }
}
