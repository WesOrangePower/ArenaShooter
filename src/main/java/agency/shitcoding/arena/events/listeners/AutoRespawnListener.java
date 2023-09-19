package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.Lobby;
import agency.shitcoding.arena.models.Ammo;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Optional;

public class AutoRespawnListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        Ammo.setAmmoForPlayer(event.getPlayer(), 0);
        Bukkit.getScheduler().scheduleSyncDelayedTask(ArenaShooter.getInstance(), () -> {
            Player player = event.getEntity();
            player.spigot().respawn();
        }, 20);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent event) {
        Player player = event.getPlayer();
        Optional<Game> gameByPlayer = GameOrchestrator.getInstance().getGameByPlayer(player);
        gameByPlayer.ifPresentOrElse(game -> {
            game.getArena().spawn(player, game);
        }, () -> {
            Lobby.getInstance().sendPlayer(player);
        });
    }
}
