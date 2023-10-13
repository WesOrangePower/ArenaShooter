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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
            case GET -> ar.getLootPoints().forEach(lp ->
                    p.sendMessage(Component.text("Point " + lp.getId() + ": " + lp.getType().name() + " at ")
                            .append(locationComponent(lp.getLocation()))
                    )
            );
        }
        // TODO
    }),
    LBOUND(a -> a == GET || a == SET, (ar, a, v, s) -> {
        if (a == GET) {
            sendBoundaries(ar, s);
            return;
        }
        if (a == SET) {
            if (!(s instanceof Player player)) {
                s.sendMessage("Players only");
                return;
            }
            Location upper = ar.getUpperBound();
            Location location = player.getLocation().toCenterLocation();

            if (location.getBlockX() >= upper.getBlockX()
                    || location.getBlockY() >= upper.getBlockY()
                    || location.getBlockZ() >= upper.getBlockZ()) {
                s.sendRichMessage("<dark_red>Lower boundary cannot have higher coordinates than upper boundary. Perhaps you meant UBOUND?");
                s.sendMessage(
                        locationComponent(location)
                                .append(Component.text(" ≥ ", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                                .append(locationComponent(upper))
                );
                return;
            }
            ar.setLowerBound(location);
            StorageProvider.getArenaStorage().storeArena(ar);
            s.sendMessage(Component.text("OK. Set lower bound to ", NamedTextColor.GREEN)
                    .append(locationComponent(location))
            );
        }
    }),
    UBOUND(a -> a == GET || a == SET, (ar, a, v, s) -> {
        if (a == GET) {
            sendBoundaries(ar, s);
            return;
        }
        if (a == SET) {
            if (!(s instanceof Player player)) {
                s.sendMessage("Players only");
                return;
            }
            Location lower = ar.getLowerBound();
            Location location = player.getLocation().toCenterLocation();

            if (location.getBlockX() <= lower.getBlockX()
                    || location.getBlockY() <= lower.getBlockY()
                    || location.getBlockZ() <= lower.getBlockZ()) {
                s.sendRichMessage("<dark_red>Upper boundary cannot have lower coordinates than lower boundary. Perhaps you meant LBOUND?");
                s.sendMessage(
                        locationComponent(location)
                                .append(Component.text(" ≤ ", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                                .append(locationComponent(lower))
                );
                return;
            }

            ar.setUpperBound(location);
            StorageProvider.getArenaStorage().storeArena(ar);
            s.sendMessage(Component.text("OK. Set upper bound to ", NamedTextColor.GREEN)
                    .append(locationComponent(location))
            );
        }
    }),
    ALLOW_HOST(a -> a == SET || a == GET, (ar, a, v, s) -> {
        if (a == GET) {
            s.sendMessage("Allow host: " + ar.isAllowHost());
            return;
        }
        if (a == SET) {
            if (v == null || !v.matches("true|false")) {
                s.sendRichMessage("<red>Invalid value. Must be `true` or `false`.");
                return;
            }
            boolean allowHost = Boolean.parseBoolean(v);
            ar.setAllowHost(allowHost);
            StorageProvider.getArenaStorage().storeArena(ar);
            s.sendRichMessage("<green>Allow host set to " + allowHost);
        }
    });

    public final Predicate<ArenaSetAction> supports;
    public final QuadConsumer<Arena, ArenaSetAction, @Nullable String, @NotNull CommandSender> applyValue;

    private static void sendBoundaries(Arena ar, CommandSender sender) {
        var l = ar.getLowerBound();
        var u = ar.getUpperBound();
        sender.sendMessage(Component.text("OK. Arena " + ar.getName() + " boundaries: ", NamedTextColor.GREEN)
                .append(locationComponent(l))
                .appendSpace()
                .append(locationComponent(u))
        );
    }

    private static Component locationComponent(Location l) {
        return Component.text("[" + l.getX() + ", " + l.getY() + ", " + l.getZ() + "]")
                .clickEvent(ClickEvent.runCommand("/tp " + l.getX() + " " + l.getY() + " " + l.getZ()));
    }
}
