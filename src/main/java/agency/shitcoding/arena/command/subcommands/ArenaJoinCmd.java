package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class ArenaJoinCmd extends CommandInst {

  public static final int OPT_ARG = 1;

  public ArenaJoinCmd(@NotNull CommandSender sender, @NotNull String[] args) {
    super(sender, args);
  }

  @Override
  public void execute() {
    if (!(sender instanceof Player player)) {
      sender.sendRichMessage("<dark_red> Only players can use this command");
      return;
    }

    GameOrchestrator gameOrchestrator = GameOrchestrator.getInstance();
    if (args.length >= 2
        && args[OPT_ARG].equalsIgnoreCase("forceStart")
        && player.hasPermission(ArenaDeathMatchCommand.ADMIN_PERM)
    ) {
      forceStart(gameOrchestrator, player);
      return;
    }
    if (gameOrchestrator.getGameByPlayer(player).isPresent()) {
      sender.sendRichMessage("<dark_red>Вы уже в игре");
      return;
    }
    Optional<Game> first = gameOrchestrator.getGames().stream().findFirst();
    if (first.isPresent()) {
      Game game = first.get();
      if (game instanceof TeamGame teamGame) {
        Optional<ETeam> team = parseTeam();
        if (team.isEmpty()) {
          String values = String.join(", ", Arrays.stream(ETeam.values()).map(Enum::name).toList());
          sender.sendRichMessage("<dark_red>Запущена командная игра. " +
              "Выберите команду: <yellow>/arena join <команда><dark_red>. " +
              "Доступные команды: <yellow>[" + String.join(", ", values) + "]");
          return;
        }
        teamGame.addPlayer(player, team.get());
      } else {
        game.addPlayer(player);
      }
      return;
    }
    sender.sendRichMessage("<dark_red>Нет доступных игр. Используйте /arena host");
  }

  private Optional<ETeam> parseTeam() {
    if (args.length < OPT_ARG + 1) {
      return Optional.empty();
    }
    try {
      return Optional.of(ETeam.valueOf(args[OPT_ARG].toUpperCase()));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  private void forceStart(GameOrchestrator gameOrchestrator, Player player) {
    gameOrchestrator.getGameByPlayer(player)
        .ifPresent(Game::startGame);
  }

}
