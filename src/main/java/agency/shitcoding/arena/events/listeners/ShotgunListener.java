package agency.shitcoding.doublejump.events.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class ShotgunListener implements Listener {

    public static final Material SHOTGUN = Material.WOODEN_PICKAXE;
    public static final int DENSITY_FACTOR = 10;
    public static final int SCAN_LEN = 35;
    private static final int PELLETS_AMOUNT = 11;
    private static final double SPREAD = .25;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType().isAir()
                || itemInMainHand.getType() != SHOTGUN
                || player.getCooldown(SHOTGUN) > 0) {
            return;
        }

        player.setCooldown(SHOTGUN, 20);
        player.playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);

        List<LivingEntity> affectedEntities = calculatePellets(player);
        // sum knockback and damage
        Map<LivingEntity, Double> damageMap = new HashMap<>();
        Map<LivingEntity, Double> knockbackMap = new HashMap<>();

        affectedEntities.forEach(entity -> {
            damageMap.put(entity, damageMap.getOrDefault(entity, 0d) + 2);
            knockbackMap.put(entity, knockbackMap.getOrDefault(entity, 0d) + .1);
        });

        if (damageMap.keySet().size() == 1 && affectedEntities.size() == PELLETS_AMOUNT) {
            damageMap.keySet().stream().findFirst().ifPresent(e -> {
                e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2, false, false, false));
                e.setFireTicks(20 * 3);
            });
        }

        damageMap.forEach((entity, damage) -> {
            entity.damage(damage, player);
            Vector knockback = entity.getLocation()
                    .subtract(player.getLocation()).toVector().normalize().multiply(knockbackMap.get(entity));
            entity.setVelocity(knockback);
        });

    }

    private List<LivingEntity> calculatePellets(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector lookingVector = eyeLocation.getDirection();
        List<LivingEntity> hits = new ArrayList<>();
        for (int i = 0; i < PELLETS_AMOUNT; i++) {
            Vector pellet = lookingVector.clone().add(new Vector(
                    Math.random() * SPREAD - SPREAD / 2,
                    Math.random() * SPREAD - SPREAD / 2,
                    Math.random() * SPREAD - SPREAD / 2
            ));
            Optional<LivingEntity> hit = traceVector(player, eyeLocation, pellet.clone());
            hit.ifPresent(hits::add);
        }
        return hits;
    }

    private Optional<LivingEntity> traceVector(Player player, Location startPoint, Vector normalizedVec) {
        for (int i = 0; i < normalizedVec.length() * SCAN_LEN * DENSITY_FACTOR; i++) {
            Location at = startPoint.clone().add(normalizedVec.clone().multiply(i / DENSITY_FACTOR));

            // TRACES
            at.getWorld().spawnParticle(Particle.WAX_ON, at, 1, 0, 0, 0, 0);

            if (at.getBlock().getType().isCollidable()) {
                // spawn smoke at the surface of block
                Location surface = startPoint.clone().add(normalizedVec.clone().multiply((i - 1) / DENSITY_FACTOR));
                if (Math.random() < .3) {
                    surface.getWorld().spawnParticle(Particle.FLAME, surface, 1, 0, 0, 0, 0);
                }
                surface.getWorld().spawnParticle(Particle.BLOCK_DUST, surface, 5, .1, .1, .1, at.getBlock().getBlockData());
                surface.getWorld().spawnParticle(Particle.SMOKE_NORMAL, surface, 3, .01, .01, .01, .01);
                break;
            }

            Optional<LivingEntity> first = at.getWorld().getNearbyEntities(at, .2, .2, .2)
                    .stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> entity != player)
                    .map(entity -> (LivingEntity) entity)
                    .findFirst();
            if (first.isPresent()) {
                at.getWorld().spawnParticle(Particle.BLOCK_DUST, at, 10, .2, .2, .2, Material.REDSTONE_BLOCK.createBlockData());
                return first;
            }
        }
        return Optional.empty();
    }
}
