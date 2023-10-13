package agency.shitcoding.arena;

import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetAction;
import agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetField;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.storage.StorageProvider;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
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
            default -> null;
        };

    }

    private List<String> resolveAdmin() {
        if (args.length == 1) {
            return List.of("set", "create", "host", "join", "leave");
        }

        return switch (args[0].toLowerCase()) {
            case "set" -> resolveSet();
//            case "create" -> List.of("name");
            case "host" -> resolveHost();
            default -> null;
        };
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
        return null;
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
                return null;
            }
            return Arrays.stream(ArenaSetField.values())
                    .filter(f -> f.supports.test(action))
                    .map(Enum::name)
                    .toList();
        }
        return null;
    }
}
