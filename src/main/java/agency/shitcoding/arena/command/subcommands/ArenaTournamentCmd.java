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
    var subCommand = args[SUB_COMMAND_ARG];

    if (!sender.hasPermission(ADMIN_PERM)) {
      if (subCommand.equalsIgnoreCase("enroll")) {
        enroll();
        return;
      }
      help();
      return;
    }

    switch (subCommand.toLowerCase()) {
      case "create" -> createTournament();
      case "status" -> status();
      case "next" -> next();
      case "add" -> addPlayer();
      case "kick" -> kickPlayer();
      case "end" -> end();
      default -> help();
    }
  }

  private void end() {
    TournamentAccessor.getInstance()
        .getTournament()
        .ifPresentOrElse(
            t -> {
              t.endTournament();
              sender.sendMessage("Tournament ended");
            },
            () -> sender.sendMessage("There is no ongoing tournament"));
  }

  private void status() {
    TournamentAccessor.getInstance()
        .getTournament()
        .ifPresentOrElse(
            t -> {
              sender.sendMessage("Tournament status");
              sender.sendMessage("Game: " + t.getCurrentGameNumber() + "/" + t.getGameCount());
              sender.sendMessage("Rule set: " + t.getRuleSet());
              sender.sendMessage("Max player count: " + t.getMaxPlayerCount());
              sender.sendMessage(
                  "Arenas: " + Array.of(t.getArenas()).map(Arena::getName).mkString(", "));
              sender.sendMessage("Next arena: " + t.peekNextArena().getName());
              if (t.getRuleSet().getGameRules().hasTeams()) {
                var lang = new LangContext("en");
                sender.sendMessage(
                    "Teams: "
                        + t.getPlayerTeams().entrySet().stream()
                            .map(
                                e ->
                                    e.getKey()
                                        + " - "
                                        + lang.getLocalized(
                                            e.getValue().getTeamMeta().getDisplayName())));
              }
              sender.sendMessage("Players: " + String.join(", ", t.getPlayerNames()));
            },
            () -> sender.sendMessage("There is no ongoing tournament"));
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
                sender.sendMessage("<dark_red>You are already enrolled to the tournament");
                return;
              }

              if (t.getPlayerNames().size() >= t.getMaxPlayerCount()) {
                sender.sendMessage("<dark_red>Tournament is full");
                return;
              }

              t.getPlayerNames().add(player.getName());
              sender.sendMessage("<green>You have been enrolled to the tournament");
            },
            () -> sender.sendMessage("<dark_red>There is no ongoing tournament"));
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
    }
    sender.sendMessage("/arena tournament enroll - Enrolls yourself to the tournament");
  }
}
