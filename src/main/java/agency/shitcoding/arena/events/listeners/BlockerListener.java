package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.gamestate.GameOrchestrator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BlockerListener implements Listener {

    @EventHandler
    private void onDisconnect(PlayerQuitEvent event) {
        GameOrchestrator.getInstance()
                .getGameByPlayer(event.getPlayer())
                .ifPresent(game -> game.removePlayer(event.getPlayer()));
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onInventoryShenanigans(InventoryMoveItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void cancelDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            GameOrchestrator gameOrchestrator = GameOrchestrator.getInstance();
            if (gameOrchestrator.getGameByPlayer(player).isEmpty()) {
                event.setCancelled(true);
            }
        }
    }

}
