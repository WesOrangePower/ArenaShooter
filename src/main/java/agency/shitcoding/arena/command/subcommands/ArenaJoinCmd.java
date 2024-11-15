package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.TournamentAccessor;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.localization.LangPlayer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ArenaJoinCmd extends CommandInst {

  public static final int ARENA_ARG = 1;
  public static final int OPT_ARG = 2;
  public static final int MIN_ARGS = 2;
  private final GameOrchestrator gameOrchestrator = GameOrchestrator.getInstance();
  private LangPlayer lang;

  public ArenaJoinCmd(@NotNull CommandSender sender, @NotNull String[] args) {
    super(sender, args);
  }

  @Override
  public void execute() {
    if (!(sender instanceof Player player)) {
      sender.sendRichMessage("<dark_red> Only players can use this command");
      return;
    }

    this.lang = new LangPlayer(player);

    if (notValid()) {
      return;
    }

    if (gameOrchestrator.getGameByPlayer(lang.getPlayer()).isPresent()) {
      lang.sendRichLocalized("command.host.alreadyInGame");
      return;
    }

    Optional<Game> first = gameOrchestrator.getGames().stream()
        .filter(game -> game.getArena().getName().equalsIgnoreCase(args[ARENA_ARG]))
        .findFirst();
    if (first.isPresent()) {
      Game game = first.get();
      if (game instanceof TeamGame teamGame) {
        Optional<ETeam> team = parseTeam();
        if (team.isEmpty()) {
          String values = String.join(", ", Arrays.stream(ETeam.values()).map(Enum::name).toList());
          lang.sendRichLocalized("command.join.teamRequired", String.join(", ", values));
          return;
        }
        teamGame.addPlayer(player, team.get());
      } else {
        game.addPlayer(player);
      }
    }
  }

  private boolean notValid() {
    List<String> arenaNames = gameOrchestrator.getUsedArenaNames();
    String joined = String.join(", ", arenaNames);
    if (arenaNames.isEmpty()) {
      lang.sendRichLocalized("command.join.noGames");
      return true;
    }

    if (args.length < MIN_ARGS) {
      lang.sendRichLocalized("command.join.arenaRequired", joined);
      return true;
    }

    var tournament = TournamentAccessor.getInstance().getTournamentOrNull();
    if (tournament != null) {
      if (!tournament.getPlayerNames().contains(lang.getPlayer().getName())) {
        lang.sendRichLocalized("command.join.tournamentInProgress");
        return true;
      }
    }

    String arena = args[ARENA_ARG];
    if (gameOrchestrator.getUsedArenas().stream().noneMatch(a -> a.getName().equalsIgnoreCase(arena))) {
      lang.sendRichLocalized("command.join.arenaNotFound", joined);
      return true;
    }

    return false;
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

}
