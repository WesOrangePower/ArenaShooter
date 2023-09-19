package agency.shitcoding.doublejump.events.listeners;

import agency.shitcoding.doublejump.DoubleJump;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AutoRespawnListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        Bukkit.getScheduler().scheduleSyncDelayedTask(DoubleJump.getInstance(), () -> {
            Player player = event.getEntity();
            player.spigot().respawn();
        }, 20);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.clearActivePotionEffects();
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        inventory.setItem(0, new ItemStack(RocketListener.ROCKET_LAUNCHER));
        inventory.setItem(1, new ItemStack(RailListener.RAILGUN));
        inventory.setItem(2, new ItemStack(ShotgunListener.SHOTGUN));
    }

    @EventHandler
    public void gibOnPlayerOverkill(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }


    }
}
