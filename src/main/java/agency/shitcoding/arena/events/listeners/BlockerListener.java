package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.gamestate.GameOrchestrator;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BlockerListener implements Listener {

    @EventHandler
    private void onDisconnect(PlayerQuitEvent event) {
        GameOrchestrator.getInstance()
                .getGameByPlayer(event.getPlayer())
                .ifPresent(game -> game.removePlayer(event.getPlayer()));
    }

    @EventHandler
    private void onItemSwitch(PlayerItemHeldEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
            return;
        }
        PlayerInventory inventory = event.getPlayer().getInventory();
        ItemStack previous = inventory.getItem(event.getPreviousSlot());
        if (previous != null && event.getPlayer().hasCooldown(previous.getType())) {
            event.setCancelled(true);
            return;
        }
        ItemStack next = inventory.getItem(event.getNewSlot());
        int delta = event.getNewSlot() - event.getPreviousSlot();
        if (event.getPreviousSlot() == 0 && event.getNewSlot() == 8) {
            delta = -1;
        } else if (event.getPreviousSlot() == 8 && event.getNewSlot() == 0) {
            delta = 1;
        }

        if (Math.abs(delta) == 1) {
            int currentSlot = event.getPreviousSlot();
            do {
                currentSlot = (currentSlot + delta) % 9;
                ItemStack item = inventory.getItem(currentSlot);
                if (item != null) {
                    event.getPlayer().getInventory().setHeldItemSlot(currentSlot);
                    break;
                }
            } while (currentSlot != event.getPreviousSlot());
        }

        if (next == null) {
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
