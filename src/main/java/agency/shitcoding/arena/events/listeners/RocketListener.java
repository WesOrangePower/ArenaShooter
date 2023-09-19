package agency.shitcoding.doublejump.events.listeners;

import agency.shitcoding.doublejump.DoubleJump;
import agency.shitcoding.doublejump.events.GameDamageEvent;
import agency.shitcoding.doublejump.models.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class RocketListener implements Listener {

    public static final Material ROCKET_LAUNCHER = Weapon.ROCKET_LAUNCHER.item;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType().isAir()
                || itemInMainHand.getType() != ROCKET_LAUNCHER
                || player.getCooldown(ROCKET_LAUNCHER) > 0) {
            return;
        }

        player.setCooldown(ROCKET_LAUNCHER, 16);

        Vector lookingVector = player.getEyeLocation().getDirection();

        player.launchProjectile(LargeFireball.class, lookingVector, rocket -> {
            rocket.setIsIncendiary(false);
            rocket.setYield(0f);
            rocket.setDirection(lookingVector.clone().multiply(1.5));
            Bukkit.getScheduler().runTaskLater(DoubleJump.getInstance(), rocket::remove, 20 * 10);
        });

        event.setCancelled(true);
    }

    @EventHandler
    public void onFireballCollision(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof LargeFireball rocket)) {
            return;
        }
        event.setCancelled(true);

        Location at = rocket.getLocation();
        rocket.remove();

        at.getWorld().createExplosion(at, 1f, false, false);
        at.getNearbyLivingEntities(1.5, 1.5, 1.5)
                .forEach(entity -> {
                    new GameDamageEvent((Player) rocket.getShooter(), entity, 10, Weapon.ROCKET_LAUNCHER)
                            .fire();
                    Vector away = entity.getLocation().toVector().subtract(at.toVector()).normalize();
                    entity.setVelocity(away.multiply(1.5));
                });
    }
}
