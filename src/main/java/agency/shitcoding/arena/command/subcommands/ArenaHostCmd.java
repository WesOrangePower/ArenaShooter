package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.Lobby;
import agency.shitcoding.arena.gamestate.TournamentAccessor;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.storage.StorageProvider;
import java.util.Arrays;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ArenaHostCmd extends CommandInst {

  public static final int ARG_RULESET = 1;
  public static final int ARG_ARENA = 2;
  public static final int ARG_TEAM = 3;
  public static final int ARG_MIN_LEN = 3;

  private RuleSet ruleSet;
  private Arena arena;

  private ETeam team;

  public ArenaHostCmd(@NotNull CommandSender sender, @NotNull String[] args) {
    super(sender, args);
  }

  @Override
  public void execute() {
    if (validate()) {
      hostGame();
    }
  }

  private void hostGame() {
    hostGameSync();
  }

  private void hostGameSync() {
    String broadcastKey;
    Game game = GameOrchestrator.getInstance().createGame(ruleSet, arena, (Player) sender);
    if (game instanceof TeamGame teamGame) {
      teamGame.addPlayer((Player) sender, team);
      broadcastKey = "command.host.broadcast.team";
    } else {
      game.addPlayer((Player) sender);
      broadcastKey = "command.host.broadcast";
    }


    Lobby.getInstance().getPlayersInLobby().stream().map(LangPlayer::new)
        .forEach(player -> player.sendRichLocalized(
            broadcastKey,
            sender.getName(), player.getLocalized(ruleSet.getName()), arena.getName()
        ));
  }

  private boolean validate() {
    if (args.length < ARG_MIN_LEN) {
      sender.sendRichMessage(
          "<red>Invalid number of arguments. Usage: <yellow>/arena host <rule set> <arena>");
      return false;
    }
    if (!(sender instanceof Player)) {
      sender.sendRichMessage("<red>Only players can host games.");
      return false;
    }
    LangPlayer lang = new LangPlayer((Player) sender);

    if (TournamentAccessor.getInstance().hasTournament()) {
      lang.sendRichLocalized("command.host.tournamentInProgress");
      return false;
    }

    try {
      ruleSet = RuleSet.valueOf(args[ARG_RULESET].toUpperCase());
    } catch (IllegalArgumentException e) {
      lang.sendRichLocalized("command.host.ruleSetNotFound", Arrays.toString(RuleSet.values()));
      return false;
    }

    arena = StorageProvider.getArenaStorage().getArena(args[ARG_ARENA]);
    if (arena == null) {
      String[] arenas = StorageProvider.getArenaStorage().getArenas().stream()
          .filter(Arena::isAllowHost)
          .map(Arena::getName)
          .toArray(String[]::new);

      lang.sendRichLocalized("command.host.arenaNotFound", Arrays.toString(arenas));
      return false;
    }

    if (!arena.getSupportedRuleSets().contains(ruleSet)) {
      var rulesetName = lang.getLocalized(ruleSet.getName());
      lang.sendRichLocalized("command.host.ruleSetNotSupportedByArena", rulesetName, arena.getName());
      return false;
    }

    if (!arena.isAllowHost() && !sender.hasPermission(ArenaDeathMatchCommand.ADMIN_PERM)) {
      lang.sendRichLocalized("command.host.arenaNotAllowed");
      return false;
    }

    boolean isTeamGame = ruleSet.getDefaultGameRules().hasTeams();
    if (isTeamGame && args.length < ARG_TEAM + 1) {
      lang.sendRichLocalized("command.host.teamArgumentRequired", ruleSet.name(), arena.getName());
      return false;
    }
    if (isTeamGame) {
      try {
        team = ETeam.valueOf(args[ARG_TEAM].toUpperCase());
      } catch (IllegalArgumentException e) {
        sender.sendRichMessage(
            "<red>Invalid team. Valid teams are: <yellow>" + Arrays.toString(ETeam.values()));
        return false;
      }
    }

    return true;
  }
}
