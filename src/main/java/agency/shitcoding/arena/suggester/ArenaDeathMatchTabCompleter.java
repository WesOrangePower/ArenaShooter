package agency.shitcoding.arena.suggester;

import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetAction;
import agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetField;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.TournamentAccessor;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.Powerup;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.models.Tournament;
import agency.shitcoding.arena.storage.StorageProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
public class ArenaDeathMatchTabCompleter {

  private final CommandSender sender;
  private final String[] args;

  private final Suggester tournamentSuggester =
      SuggesterBuilder.builder()
          .at(2)
          .inCase((s, a) -> s.hasPermission(ArenaDeathMatchCommand.ADMIN_PERM))
          .suggest(
              () ->
                  List.of("create", "next", "add", "status", "kick", "end", "enroll", "gameRules"))

          // create
          .at(3)
          .inCaseArgIsIgnoreCase(1, "create")
          .suggest(() -> List.of("public", "private"))
          .at(4)
          .inCaseArgIsIgnoreCase(1, "create")
          .suggestEnumLower(() -> RuleSet.class)
          .at(5)
          .inCaseArgIsIgnoreCase(1, "create")
          .suggestInts(() -> range(1, 10))
          .at(6)
          .inCaseArgIsIgnoreCase(1, "create")
          .suggestInts(() -> range(2, 16))
          .addRules(trailingArenaRules())
          .rule()
          .inCaseArgIsIgnoreCase(1, "create")
          .inCase((s, a) -> a.length >= 6)
          .suggest(
              () ->
                  StorageProvider.getArenaStorage().getArenas().stream()
                      .map(Arena::getName)
                      .toList())

          // add
          .at(3)
          .inCaseArgIsIgnoreCase(1, "add")
          .suggestPlayers()
          .at(4)
          .inCaseArgIsIgnoreCase(1, "add")
          .inCase(
              (s, a) ->
                  TournamentAccessor.getInstance()
                      .getTournament()
                      .map(Tournament::isTeamTournament)
                      .orElse(false))
          .suggestEnumLower(() -> ETeam.class)
          .at(3)
          .inCaseArgIsIgnoreCase(1, "kick")
          .suggest(
              () ->
                  TournamentAccessor.getInstance()
                      .getTournament()
                      .map(Tournament::getPlayerNames)
                      .orElse(List.of()))
          .build();

  private List<SuggestionRule> trailingArenaRules() {
    var rules = new ArrayList<SuggestionRule>();
    var arenas = StorageProvider.getArenaStorage().getArenas();

    for (Arena arena : arenas) {
      rules.add(
          new SuggestionRule(
              (s, a) -> {
                if (a.length < 6) {
                  return false;
                }
                RuleSet rulesSet;
                try {
                  rulesSet = RuleSet.valueOf(a[3].toUpperCase());
                } catch (IllegalArgumentException e) {
                  throw new RuntimeException(e);
                }
                return arena.getSupportedRuleSets().contains(rulesSet);
              },
              () -> List.of(arena.getName())));
    }

    return rules;
  }

  public @Nullable List<String> onTabComplete() {
    boolean isAdmin = sender.hasPermission(ArenaDeathMatchCommand.ADMIN_PERM);
    if (isAdmin) {
      return resolveAdmin();
    }
    return resolveNonAdmin();
  }

  private List<String> resolveNonAdmin() {
    if (args.length == 1) {
      var list = new ArrayList<>(List.of("join", "host", "leave"));
      if (TournamentAccessor.getInstance().hasTournament()) {
        list.add("tournament");
      }
      return list;
    }

    return switch (args[0].toLowerCase()) {
      case "host" -> resolveHost();
      case "join" -> resolveJoin();
      default -> null;
    };
  }

  private List<String> resolveAdmin() {
    if (args.length == 1) {
      return List.of("set", "create", "host", "join", "leave", "utils", "forceStart", "tournament");
    }

    return switch (args[0].toLowerCase()) {
      case "set" -> resolveSet();
      case "host" -> resolveHost();
      case "join" -> resolveJoin();
      case "utils" -> resolveUtils();
      case "forcestart" -> resolveForceStart();
      case "tournament" -> resolveTournament();
      default -> List.of();
    };
  }

  private List<String> resolveUtils() {
    if (args.length == 2) {
      return List.of("gib", "tracers", "guns", "powerup", "helix", "cutter");
    }
    if (args.length == 3) {
      if (args[1].equalsIgnoreCase("guns")) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
      } else if (args[1].equalsIgnoreCase("powerup")) {
        return Arrays.stream(Powerup.values())
            .map(Enum::name)
            .filter(p -> p.startsWith(args[2].toUpperCase()))
            .toList();
      }
    }
    return List.of();
  }

  private List<String> resolveForceStart() {
    if (args.length == 2) {
      return GameOrchestrator.getInstance().getUsedArenaNames();
    }
    return List.of();
  }

  private List<String> resolveTournament() {
    return tournamentSuggester.suggest(sender, args);
  }

  private List<String> resolveJoin() {
    if (args.length == 2) {
      return GameOrchestrator.getInstance().getUsedArenaNames();
    }
    if (args.length == 3) {
      return GameOrchestrator.getInstance()
          .getGameByArena(args[1])
          .filter(TeamGame.class::isInstance)
          .map(
              game ->
                  Arrays.stream(ETeam.values()).map(Enum::name).map(String::toLowerCase).toList())
          .orElse(null);
    }
    return List.of();
  }

  private List<String> resolveHost() {
    if (args.length == 2) {
      return Arrays.stream(RuleSet.values()).map(Enum::name).map(String::toLowerCase).toList();
    }
    if (args.length == 3) {
      RuleSet rulesSet;
      try {
        rulesSet = RuleSet.valueOf(args[1].toUpperCase());
      } catch (IllegalArgumentException e) {
        return List.of();
      }
      return StorageProvider.getArenaStorage().getArenas().stream()
          .filter(arena -> arena.getSupportedRuleSets().contains(rulesSet))
          .map(Arena::getName)
          .toList();
    }
    return List.of();
  }

  private List<String> resolveSet() {
    if (args.length == 2) {
      return StorageProvider.getArenaStorage().getArenas().stream().map(Arena::getName).toList();
    }
    if (args.length == 3) {
      return Arrays.stream(ArenaSetAction.values())
          .map(Enum::name)
          .map(String::toLowerCase)
          .toList();
    }
    if (args.length == 4) {
      ArenaSetAction action;
      try {
        action = ArenaSetAction.valueOf(args[2].toUpperCase());
      } catch (IllegalArgumentException e) {
        return List.of();
      }
      return Arrays.stream(ArenaSetField.values())
          .filter(f -> f.supports.test(action))
          .map(Enum::name)
          .toList();
    }
    return List.of();
  }
}
