package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

import static agency.shitcoding.arena.GameplayConstants.MACHINE_GUN_DAMAGE;


public class MachineGunListener implements Listener {
    public static final int DENSITY_FACTOR = 5;
    public static final int SCAN_LEN = 16;
    private static final Material MACHINE = Weapon.MACHINE_GUN.item;

    @EventHandler
    public void onPlayerInteract(GameShootEvent event) {
        Player player = event.getParentEvent().getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType().isAir()
                || itemInMainHand.getType() != MACHINE
                || player.getCooldown(MACHINE) > 0) {
            return;
        }
        Weapon.applyCooldown(player, Weapon.MACHINE_GUN.cooldown);
        Location eyeLocation = player.getEyeLocation();
        Vector lookingVector = eyeLocation.getDirection();
        World world = eyeLocation.getWorld();

        world.playSound(eyeLocation, Sound.BLOCK_DISPENSER_DISPENSE, 1f, 2f);

        // row of particles
        int iterations = 0;
        outer:
        for (int i = 0; i < SCAN_LEN; i++) {
            // for DENSITY_FACTOR times/block in the direction of the player's looking direction
            // spawn a particle

            for (int j = 0; j < DENSITY_FACTOR; j++) {
                iterations++;
                var at = eyeLocation.add(lookingVector.clone().normalize().multiply(i / DENSITY_FACTOR));

                if (iterations % 3 == 0) {
                    world.spawnParticle(Particle.ASH, at, 1, 0, 0, 0, 0);
                }

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
                            world.spawnParticle(Particle.DAMAGE_INDICATOR, at, 1, 0, 0, 0);
                        });
                affectedEntities.forEach(entity -> new GameDamageEvent(player, entity, MACHINE_GUN_DAMAGE, Weapon.MACHINE_GUN)
                        .fire());
                if (!affectedEntities.isEmpty()) {
                    break outer;
                }
            }
        }
    }
}
