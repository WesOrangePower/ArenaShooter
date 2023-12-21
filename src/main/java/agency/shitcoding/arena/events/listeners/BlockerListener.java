package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.gamestate.GameOrchestrator;
import org.bukkit.GameMode;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class BlockerListener implements Listener {

    @EventHandler
    private void onDisconnect(PlayerQuitEvent event) {
        GameOrchestrator.getInstance()
                .getGameByPlayer(event.getPlayer())
                .ifPresent(game -> game.removePlayer(event.getPlayer()));
    }

    @EventHandler
    private void onLeftClick(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.ADVENTURE && event.getAction().isLeftClick()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onHandSwap(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void onFireballReflect(EntityDamageEvent event) {
        if (event.getEntity() instanceof Fireball) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInventoryOpen(InventoryOpenEvent event) {
        HumanEntity entity = event.getPlayer();
        if (!(entity instanceof Player player)) {
            return;
        }
        boolean isInGame = GameOrchestrator.getInstance().getGameByPlayer(player).isPresent();
        if (isInGame) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInventoryInteract(InventoryInteractEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player player)) {
            return;
        }
        boolean isInGame = GameOrchestrator.getInstance().getGameByPlayer(player).isPresent();
        if (isInGame) {
            event.setCancelled(true);
        }
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
