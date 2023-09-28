package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.command.Conf;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BoundingBox;

public class Lobby {
    private static Lobby instance;

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
                .getLocation(Conf.lobbyLocation, new Location(Bukkit.getWorld("world"), 0, 64, 0));
    }

    public BoundingBox getBoundaries() {
        return BoundingBox.of(getLocation(), 25d, 25d, 25d);
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
    }
}
