package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.command.Conf;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Lobby {
    private static Lobby instance;

    private ItemStack itemStack;

    private Lobby() {
    }

    public static Lobby getInstance() {
        if (instance == null) {
            instance = new Lobby();
        }
        return instance;
    }

    public Location getLocation() {
        return ArenaShooter.getInstance().getConfig()
                .getLocation(Conf.lobbyLocation, new Location(Bukkit.getWorld("world"), -0.5d, 64, -0.5d));
    }

    public BoundingBox getBoundaries() {
        return BoundingBox.of(getLocation(), 100d, 50d, 100d);
    }

    public void sendPlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.clearActivePotionEffects();
        player.teleport(getLocation());
        player.setGameMode(GameMode.ADVENTURE);
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        inventory.setItem(4, getLobbyItem());
    }

    public Set<Player> getPlayersInLobby() {
        List<Player> gamePlayers = GameOrchestrator.getInstance().getGames().stream()
                .flatMap(game -> game.getPlayers().stream())
                .toList();
        return Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> !gamePlayers.contains(player))
                .collect(Collectors.toUnmodifiableSet());
    }

    public ItemStack getLobbyItem() {
        if (itemStack != null) {
            return itemStack;
        }

        var material = Material.NETHER_STAR;
        var item = new ItemStack(material);

        item.editMeta(meta -> {
            meta.displayName(Component.text("Меню", NamedTextColor.AQUA, TextDecoration.BOLD));
            meta.lore(List.of(
                    Component.text("Нажмите ПКМ, чтобы открыть меню", NamedTextColor.GRAY)
            ));
        });

        itemStack = item;

        return itemStack;
    }
}
