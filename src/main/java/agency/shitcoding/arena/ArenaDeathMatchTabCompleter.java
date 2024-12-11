package agency.shitcoding.arena;

import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetAction;
import agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetField;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.Powerup;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.storage.StorageProvider;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class ArenaDeathMatchTabCompleter {

  private final CommandSender sender;
  private final String[] args;


  public @Nullable List<String> onTabComplete() {
    boolean isAdmin = sender.hasPermission(ArenaDeathMatchCommand.ADMIN_PERM);
    if (isAdmin) {
      return resolveAdmin();
    }
    return resolveNonAdmin();
  }

  private List<String> resolveNonAdmin() {
    if (args.length == 1) {
      return List.of("join", "host", "leave");
    }

    return switch (args[0].toLowerCase()) {
      case "host" -> resolveHost();
      case "join" -> resolveJoin();
      default -> null;
    };
  }

  private List<String> resolveAdmin() {
    if (args.length == 1) {
      return List.of("set", "create", "host", "join", "leave", "utils", "forceStart");
    }

    return switch (args[0].toLowerCase()) {
      case "set" -> resolveSet();
      case "host" -> resolveHost();
      case "join" -> resolveJoin();
      case "utils" -> resolveUtils();
      case "forcestart" -> resolveForceStart();
      default -> List.of();
    };
  }

  private List<String> resolveUtils() {
    if (args.length == 2) {
      return List.of("gib", "tracers", "guns", "powerup", "helix");
    }
    if (args.length == 3) {
      if (args[1].equalsIgnoreCase("guns")) {
        return Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .toList();
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

  private List<String> resolveJoin() {
    if (args.length == 2) {
      return GameOrchestrator.getInstance().getUsedArenaNames();
    }
    if (args.length == 3) {
      return GameOrchestrator.getInstance().getGameByArena(args[1])
          .filter(TeamGame.class::isInstance)
          .map(game -> Arrays.stream(ETeam.values())
                  .map(Enum::name)
                  .map(String::toLowerCase)
                  .toList()
          ).orElse(null);
    }
    return List.of();
  }

  private List<String> resolveHost() {
    if (args.length == 2) {
      return Arrays.stream(RuleSet.values())
          .map(Enum::name)
          .map(String::toLowerCase)
          .toList();
    }
    if (args.length == 3) {
      return StorageProvider.getArenaStorage().getArenas().stream()
          .map(Arena::getName)
          .toList();
    }
    return List.of();
  }

  private List<String> resolveSet() {
    if (args.length == 2) {
      return StorageProvider.getArenaStorage().getArenas().stream()
          .map(Arena::getName)
          .toList();
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
