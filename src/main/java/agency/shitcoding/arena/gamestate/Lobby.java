package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.command.Conf;
import agency.shitcoding.arena.events.listeners.DamageListener;
import agency.shitcoding.arena.localization.LangPlayer;
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
    DamageListener.setBaseHealth(player);
    player.setHealth(20);
    player.setFoodLevel(20);
    player.setLevel(0);
    player.setExp(0f);
    player.clearActivePotionEffects();
    player.teleport(getLocation());
    player.setGameMode(GameMode.ADVENTURE);
    PlayerInventory inventory = player.getInventory();
    inventory.clear();
    inventory.setArmorContents(null);
    inventory.setItem(4, getLobbyItem(player));
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

  public ItemStack getLobbyItem(Player player) {
    var langPlayer = new LangPlayer(player);

    var material = Material.NETHER_STAR;
    var item = new ItemStack(material);

    item.editMeta(meta -> {
      meta.displayName(Component.text(langPlayer.getLocalized("lobby.menu"), NamedTextColor.AQUA, TextDecoration.BOLD));
      meta.lore(List.of(
          Component.text(langPlayer.getLocalized("menu.lore.open"), NamedTextColor.GRAY)
      ));
    });

    return item;
  }
}
