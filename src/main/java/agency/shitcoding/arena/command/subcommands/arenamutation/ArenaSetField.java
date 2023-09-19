package agency.shitcoding.arena.command.subcommands.arenamutation;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.storage.ArenaStorage;
import agency.shitcoding.arena.storage.StorageProvider;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static agency.shitcoding.arena.command.subcommands.arenamutation.ArenaMutationAction.*;

@RequiredArgsConstructor
public enum ArenaMutationField {
    NAME(a -> a == SET, (v, p) -> {
        if (v == null || v.matches("[a-zA-Z0-9_]{3,16}")) {
            p.sendRichMessage("<red>Invalid arena name. Must be between 3 and 16 characters long and contain only letters, numbers and underscores.");
            return;
        }
        ArenaStorage arenaStorage = StorageProvider.getArenaStorage();
        Arena arena = arenaStorage.getArena(v);
        if (arena == null) {
            p.sendRichMessage("<red>Arena with that name already exists.");
            return;
        }
        arenaStorage.deleteArena(arena);
        arena.setName(v);
        arenaStorage.storeArena(arena);
    }),
    POWERUP(a -> a == ADD || a == REMOVE )



    public final Predicate<ArenaMutationAction> supports;
    public final BiConsumer<@Nullable String, @NotNull CommandSender> applyValue;
}
