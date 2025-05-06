package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.tutorial.TutorialArenaFactory;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.RuleSet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ArenaTutorialCmd extends CommandInst {

  public ArenaTutorialCmd(@NotNull CommandSender sender, @NotNull String[] args) {
    super(sender, args);
  }

  @Override
  public void execute() {
    if (validate()) {
      var arena = TutorialArenaFactory.getTutorialArena();
      if (arena == null) {
        LangPlayer.of((Player) sender).sendRichLocalized("command.tutorial.notSetUp");
        return;
      }
      var game = GameOrchestrator.getInstance()
          .createGame(RuleSet.TUTORIAL, arena, RuleSet.TUTORIAL.getDefaultGameRules());
      game.addPlayer((Player)sender);
      game.forceStart();
    }
  }

  private boolean validate() {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("<dark_red> This command can only be used by players.");
      return false;
    }
    if (GameOrchestrator.getInstance().getGameByPlayer(player).isPresent()) {
      LangPlayer.of(player).sendRichLocalized("command.tutorial.alreadyInGame");
      return false;
    }
    return true;
  }
}
