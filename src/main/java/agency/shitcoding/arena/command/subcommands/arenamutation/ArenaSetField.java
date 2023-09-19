package agency.shitcoding.arena.command.subcommands.arenamutation;

import agency.shitcoding.arena.QuadConsumer;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.LootPoint;
import agency.shitcoding.arena.models.Powerup;
import agency.shitcoding.arena.storage.ArenaStorage;
import agency.shitcoding.arena.storage.StorageProvider;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;

import static agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetAction.*;

@RequiredArgsConstructor
public enum ArenaSetField {
    NAME(a -> a == SET, (ar, a, v, p) -> {
        if (v == null || !v.matches("[a-zA-Z0-9_]{3,16}")) {
            p.sendRichMessage("<red>Invalid arena name. Must be between 3 and 16 characters long and contain only letters, numbers and underscores.");
            return;
        }
        ArenaStorage arenaStorage = StorageProvider.getArenaStorage();
        if (arenaStorage.getArena(v) != null) {
            p.sendRichMessage("<red>Arena with that name already exists.");
            return;
        }
        arenaStorage.deleteArena(ar);
        ar.setName(v);
        arenaStorage.storeArena(ar);
        p.sendRichMessage("<green>Arena name changed to " + v);
    }),
    POWERUP(a -> a == ADD || a == REMOVE || a == GET, (ar, a, v, p) -> {
        ArenaStorage arenaStorage = StorageProvider.getArenaStorage();
        switch (a) {
            case ADD -> {
                Powerup powerup;
                try {
                    powerup = Powerup.valueOf(v);
                } catch (IllegalArgumentException e) {
                    p.sendRichMessage("<red>Invalid powerup name. Valid powerups are: " + Arrays.toString(Powerup.values()));
                    return;
                }
                Location centerLocation = ((Player) p).getLocation().toCenterLocation();
                LootPoint lootPoint = new LootPoint(ar.getLootPoints().size(), centerLocation, powerup);
                ar.getLootPoints().add(lootPoint);
                arenaStorage.storeArena(ar);
                p.sendRichMessage("<green>Powerup " + powerup.name() + " added to arena " + ar.getName() + " at " + centerLocation);
            }
            case GET -> {
                ar.getLootPoints().forEach( lp ->
                        p.sendMessage(Component.text("Point " + lp.getId() + ": " + lp.getType().name() + " at ")
                                .append(Component.text("[" + lp.getLocation().getX() + ", " + lp.getLocation().getY() + ", " + lp.getLocation().getZ() + "]")
                                        .clickEvent(ClickEvent.runCommand("/tp " + lp.getLocation().getX() + " " + lp.getLocation().getY() + " " + lp.getLocation().getZ()))
                                )
                        )
                );
            }
        }
        // TODO
    });



    public final Predicate<ArenaSetAction> supports;
    public final QuadConsumer<Arena, ArenaSetAction, @Nullable String, @NotNull CommandSender> applyValue;
}
