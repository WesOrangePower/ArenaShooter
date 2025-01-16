package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.gamestate.TournamentAccessor;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.models.Tournament;
import agency.shitcoding.arena.storage.ArenaStorage;
import agency.shitcoding.arena.storage.StorageProvider;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

class TournamentInputValidator {

  private TournamentInputValidator() {
  }

  public static TournamentInputValidator tournamentInputValidator() {
    return new TournamentInputValidator();
  }

  public Validation<String, Tuple2<Player, ETeam>> validateAdd(String playerName, String team) {

    var tournament = TournamentAccessor.getInstance().getTournament().orElse(null);
    if (tournament == null) {
      return Validation.invalid(
          "There is no ongoing tournament, create one with /arena tournament create");
    }

    var already =
        tournament.getPlayerNames().stream().anyMatch(p -> p.equalsIgnoreCase(playerName));
    if (already) {
      return Validation.invalid("Player is already in the tournament");
    }

    var player = Bukkit.getPlayer(playerName);
    if (player == null) {
      return Validation.invalid("Player not found");
    }

    ETeam et = null;
    if (tournament.getRuleSet().getDefaultGameRules().hasTeams()) {
      if (team == null) {
        return Validation.invalid("Team is required for this tournament");
      }
      et = Try.of(() -> ETeam.valueOf(team.toUpperCase())).getOrElse((ETeam) null);
      if (et == null) {
        return Validation.invalid("Invalid team");
      }
    }

    var result = new Tuple2<>(Bukkit.getPlayer(playerName), et);
    return Validation.valid(result);
  }

  public Validation<String, Void> validateHostNextMap() {
    return TournamentAccessor.getInstance().getTournament()
        .map(t -> t.getCurrentGameNumber() < t.getGameCount())
        .orElse(false)
        ? Validation.valid(null)
        : Validation.invalid("Tournament not ongoing or its game limit has been reached");
  }

  public Validation<Seq<String>, Tournament> validateCreate(
      String joinType,
      String ruleSet,
      Try<Integer> gameCount,
      Try<Integer> maxPlayers,
      List<String> arenas) {
    return Validation.combine(
            validateNoOngoingTournament(),
            validateJoinType(joinType),
            validateRuleSet(ruleSet),
            validateGameCount(gameCount),
            validateMaxPlayers(maxPlayers),
            validateArenas(arenas))
        .ap((_1, jt, rs, gc, mp, as) -> new Tournament(jt.equals("public"), rs, gc, mp, as));
  }

  private Validation<String, Void> validateNoOngoingTournament() {
    return TournamentAccessor.getInstance().hasTournament()
        ? Validation.invalid("There is already an ongoing tournament")
        : Validation.valid(null);
  }

  private Validation<String, Integer> validateMaxPlayers(Try<Integer> maxPlayers) {
    return maxPlayers.isSuccess() && maxPlayers.get() > 0
        ? Validation.valid(maxPlayers.get())
        : Validation.invalid("Invalid max player count. Must be a positive integer");
  }

  private Validation<String, Arena[]> validateArenas(List<String> arenas) {
    ArenaStorage arenaStorage = StorageProvider.getArenaStorage();
    Arena[] foundArenas = arenas.stream().map(arenaStorage::getArena).toArray(Arena[]::new);
    return Array.of(foundArenas).contains(null) || foundArenas.length != arenas.size()
        ? Validation.invalid("Invalid arena(s)")
        : Validation.valid(foundArenas);
  }

  private Validation<String, Integer> validateGameCount(Try<Integer> gameCount) {
    return gameCount.isSuccess() && gameCount.get() > 0
        ? Validation.valid(gameCount.get())
        : Validation.invalid("Invalid game count. Must be a positive integer");
  }

  private Validation<String, RuleSet> validateRuleSet(String ruleSet) {
    var rs =
        Array.of(RuleSet.values())
            .find(r -> r.name().equalsIgnoreCase(ruleSet))
            .getOrElse((RuleSet) null);
    return rs != null
        ? Validation.valid(rs)
        : Validation.invalid(
            "Invalid rule set. Must be any of " + Arrays.toString(RuleSet.values()));
  }

  private Validation<String, String> validateJoinType(String joinType) {
    return joinType.equalsIgnoreCase("public") || joinType.equalsIgnoreCase("private")
        ? Validation.valid(joinType.toLowerCase())
        : Validation.invalid("Invalid join type. Must be 'public' or 'private'");
  }
}
