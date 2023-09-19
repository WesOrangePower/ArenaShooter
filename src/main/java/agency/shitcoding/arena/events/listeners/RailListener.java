package agency.shitcoding.doublejump.events.listeners;

import agency.shitcoding.doublejump.events.GameDamageEvent;
import agency.shitcoding.doublejump.models.Weapon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class RailListener implements Listener {
    public static final Material RAILGUN = Material.CROSSBOW;
    public static final int DENSITY_FACTOR = 5;
    public static final int SCAN_LEN = 32;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType().isAir()
                || itemInMainHand.getType() != RAILGUN
                || player.getCooldown(RAILGUN) > 0) {
            return;
        }
        player.setCooldown(RAILGUN, 30);
        Location eyeLocation = player.getEyeLocation();
        Vector lookingVector = eyeLocation.getDirection();
        World world = eyeLocation.getWorld();

        // row of particles
        int iterations = 0;
        for (int i = 0; i < SCAN_LEN; i++) {
            // for DENSITY_FACTOR times/block in the direction of the player's looking direction
            // spawn a particle

            for (int j = 0; j < DENSITY_FACTOR; j++) {
                iterations++;
                var at = eyeLocation.add(lookingVector.clone().normalize().multiply(i / DENSITY_FACTOR));
                world.spawnParticle(Particle.WAX_OFF, at, 1, 0, 0, 0, 0);

                if (at.getBlock().getType().isCollidable()) {
                    // if the block is collidable, stop the loop
                    break;
                }

                Set<LivingEntity> affectedEntities = new HashSet<>();
                at.getWorld().getNearbyEntities(at, .2, .2, .2)
                        .stream()
                        .filter(entity -> entity instanceof LivingEntity)
                        .filter(entity -> entity != player)
                        .forEach(entity -> {
                            affectedEntities.add((LivingEntity) entity);
                            world.spawnParticle(Particle.FLASH, at, 10, .2, .2, .2, 0);
                        });
                affectedEntities.forEach(entity -> {
                    new GameDamageEvent(player, entity, 10, Weapon.RAILGUN)
                            .fire();
                });
            }
        }
        player.sendRichMessage("<rainbow>iterations: " + iterations);
    }
}
