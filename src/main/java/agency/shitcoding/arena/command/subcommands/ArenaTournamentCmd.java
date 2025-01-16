package agency.shitcoding.arena.command.subcommands;

import static agency.shitcoding.arena.command.ArenaDeathMatchCommand.ADMIN_PERM;
import static agency.shitcoding.arena.command.subcommands.TournamentInputValidator.tournamentInputValidator;

import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.TournamentAccessor;
import agency.shitcoding.arena.localization.LangContext;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.Tournament;
import io.vavr.collection.Array;
import io.vavr.control.Try;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ArenaTournamentCmd extends CommandInst {

  public static final int SUB_COMMAND_ARG = 1;
  TournamentInputValidator validator = tournamentInputValidator();

  // Tournament command
  // /arena tournament create <ruleSet> <gameCount> <maxPlayerCount> <arena> [arena2...] - Creates a
  // tournament
  // /arena tournament status - Shows tournament status
  // /arena tournament next - Hosts next match
  // /arena tournament end - Ends the tournament and shows results
  // /arena tournament add <player> [team] - Adds player to the tournament
  // /arena tournament kick <player> - Removes player from the tournament

  public ArenaTournamentCmd(@NotNull CommandSender sender, @NotNull String[] args) {
    super(sender, args);
  }

  @Override
  public void execute() {
    if (args.length < SUB_COMMAND_ARG + 1) {
      help();
      return;
    }

    var subCommand = args[SUB_COMMAND_ARG].toLowerCase();

    /*
     * Non-admin commands
     */

    switch (subCommand) {
      case "enroll" -> {
        enroll();
        return;
      }
      case "leave" -> {
        leave();
        return;
      }
    }

    if (!sender.hasPermission(ADMIN_PERM)) {
      help();
      return;
    }

    /*
     * Admin commands
     */

    switch (subCommand) {
      case "create" -> createTournament();
      case "status" -> status();
      case "next" -> next();
      case "add" -> addPlayer();
      case "kick" -> kickPlayer();
      case "end" -> end();
      default -> help();
    }
  }

  private void leave() {
    TournamentAccessor.getInstance()
        .getTournament()
        .ifPresentOrElse(
            t -> {
              t.removePlayer(sender.getName());
              reply("command.tournament.leave.success");
            },
            () -> reply("command.tournament.noOngoing"));
  }

  private void end() {
    TournamentAccessor.getInstance()
        .getTournament()
        .ifPresentOrElse(
            t -> {
              t.endTournament();
              reply("command.tournament.ended");
            },
            () -> reply("command.tournament.noOngoing"));
  }

  private void status() {
    TournamentAccessor.getInstance()
        .getTournament()
        .ifPresentOrElse(
            t -> {
              reply("command.tournament.status.tournamentStatus");
              reply(
                  "command.tournament.status.gameNumber",
                  t.getCurrentGameNumber(),
                  t.getGameCount());
              reply("command.tournament.status.ruleSet", t.getRuleSet());
              reply("command.tournament.status.maxPlayers", t.getMaxPlayerCount());
              reply("command.tournament.status.type", t.isPublicJoin() ? "public" : "private");
              reply(
                  "command.tournament.status.arenas",
                  Array.of(t.getArenas()).map(Arena::getName).mkString(", "));
              reply("command.tournament.status.nextArena", t.peekNextArena().getName());
              if (t.getRuleSet().getDefaultGameRules().hasTeams()) {
                var lang = new LangContext("en");
                sender.sendMessage(
                    "Teams: "
                        + t.getPlayerTeams().entrySet().stream()
                            .map(
                                e ->
                                    e.getKey()
                                        + " - "
                                        + lang.getLocalized(
                                            e.getValue().getTeamMeta().getDisplayName()))
                            .collect(Collectors.joining(", ")));
              }
              sender.sendMessage("Players: " + String.join(", ", t.getPlayerNames()));
            },
            () -> reply("command.tournament.noOngoing"));
  }

  private void enroll() {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("Only players can enroll to the tournament");
      return;
    }

    TournamentAccessor.getInstance()
        .getTournament()
        .ifPresentOrElse(
            t -> {
              if (t.getPlayerNames().contains(player.getName())) {
                reply("command.tournament.enroll.alreadyEnrolled");
                return;
              }

              if (t.getPlayerNames().size() >= t.getMaxPlayerCount()) {
                reply("command.tournament.enroll.full");
                return;
              }

              if (!t.isPublicJoin()) {
                reply("command.tournament.enroll.nonPublic");
                return;
              }

              t.addPlayer(player, t.nextAutoAssignedTeam());
              reply("command.tournament.enroll.success");
            },
            () -> reply("command.tournament.noOngoing"));
  }

  private void kickPlayer() {
    final int playerNameArg = 2;

    if (args.length < 3) {
      help();
      return;
    }

    var playerName = args[playerNameArg];

    var isInTournament =
        TournamentAccessor.getInstance()
            .getTournament()
            .map(t -> t.removePlayer(playerName))
            .orElse(false);

    if (!isInTournament) {
      sender.sendRichMessage("<dark_red>Player not found in the tournament");
      return;
    }

    sender.sendRichMessage("<green>Player removed from the tournament");
  }

  private void addPlayer() {
    final int playerNameArg = 2;
    final int teamArg = 3;

    if (args.length < 3) {
      help();
      return;
    }

    var playerName = args[playerNameArg];
    var team = args.length > 3 ? args[teamArg] : null;

    var validationResult = validator.validateAdd(playerName, team);
    if (validationResult.isInvalid()) {
      sender.sendRichMessage("<dark_red> Player addition failed: " + validationResult.getError());
      return;
    }

    var playerTeam = validationResult.get();

    TournamentAccessor.getInstance()
        .getTournament()
        .ifPresentOrElse(
            t -> {
              var result = t.addPlayer(playerTeam._1, playerTeam._2);
              if (result.isLeft()) {
                sender.sendRichMessage("<dark_red> Player addition failed: " + result.getLeft());
              } else {
                sender.sendRichMessage("<green> Player" + playerTeam._1 + " added to the tournament");
              }
            },
            () -> sender.sendMessage("<dark_red>There is no ongoing tournament"));
  }

  private void next() {

    var result = validator.validateHostNextMap();

    if (result.isInvalid()) {
      sender.sendRichMessage("<dark_red>Host failed: " + result.getError());
      return;
    }

    TournamentAccessor.getInstance()
        .getTournament()
        .ifPresentOrElse(
            tournament -> {
              var currentGame = tournament.getCurrentGame();
              if (currentGame != null) {
                currentGame.endGame("game.end.tournamentHostedNextMap", false);
              }
              tournament.hostNextGame();
            },
            () -> sender.sendMessage("<dark_red>There is no ongoing tournament"));
  }

  private void createTournament() {
    final int joinTypeArg = 2;
    final int ruleSetArg = 3;
    final int gameCountArg = 4;
    final int maxPlayerCount = 5;
    final int arenasStartArg = 6;

    if (args.length < 7) {
      help();
      return;
    }

    var joinType = args[joinTypeArg];
    var ruleSet = args[ruleSetArg];
    var gameCount = Try.of(() -> Integer.parseInt(args[gameCountArg]));
    var maxPlayers = Try.of(() -> Integer.parseInt(args[maxPlayerCount]));
    var arenas = Array.of(args).subSequence(arenasStartArg, args.length).toJavaList();

    var validationResult =
        validator.validateCreate(joinType, ruleSet, gameCount, maxPlayers, arenas);
    if (validationResult.isInvalid()) {
      sender.sendRichMessage(
          "<dark_red>Tournament creation failed: " + validationResult.getError().mkString(", "));
      return;
    }

    var result = validationResult.get();
    TournamentAccessor.getInstance().setTournament(result);
    sender.sendRichMessage("<green>Tournament created");
    announceTournament(result);
    for (Game game : GameOrchestrator.getInstance().getGames()) {
      game.endGame("game.end.tournamentStart", false);
    }
  }

  private void announceTournament(Tournament result) {
    for (LangPlayer lp : Bukkit.getOnlinePlayers().stream().map(LangPlayer::new).toList()) {
      var ruleSet = lp.getLocalized(result.getRuleSet().getName());
      var gameCount = result.getGameCount();
      var announcement =
          lp.getRichLocalized("tournament.announce", sender.getName(), ruleSet, gameCount);
      lp.getPlayer().sendMessage(announcement);

      if (result.isPublicJoin()) {
        lp.sendRichLocalized("tournament.announce.public");
      }
    }
  }

  private void help() {
    sender.sendMessage("Tournament command");
    if (sender.hasPermission(ADMIN_PERM)) {
      sender.sendMessage(
          "/arena tournament create <public|private> <ruleSet> <gameCount> <maxPlayerCount> <arena> [arena2...] - Creates a tournament");
      sender.sendMessage("/arena tournament next - Hosts next match");
      sender.sendMessage("/arena tournament add <player> [team] - Adds player to the tournament");
      sender.sendMessage("/arena tournament kick <player> - Removes player from the tournament");
      sender.sendMessage("/arena tournament end - Ends the tournament");
    }
    sender.sendMessage("/arena tournament enroll - Enrolls yourself to the tournament");
  }

  private void reply(String key, Object... args) {
    if (sender instanceof Player player) {
      LangPlayer.of(player).sendRichLocalized(key, args);
    } else {
      sender.sendRichMessage(new LangContext().getLocalized(key, args));
    }
  }
}
