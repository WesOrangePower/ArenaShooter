package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class RocketListener implements Listener {

    public static final Material ROCKET_LAUNCHER = Weapon.ROCKET_LAUNCHER.item;

    @EventHandler
    public void onShooting(GameShootEvent event) {
        Player player = event.getParentEvent().getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType().isAir()
                || itemInMainHand.getType() != ROCKET_LAUNCHER
                || player.getCooldown(ROCKET_LAUNCHER) > 0) {
            return;
        }

        Weapon.applyCooldown(player, Weapon.ROCKET_LAUNCHER.cooldown);

        Vector lookingVector = player.getEyeLocation().getDirection();

        player.launchProjectile(LargeFireball.class, lookingVector, rocket -> {
            rocket.setIsIncendiary(false);
            rocket.setYield(0f);
            rocket.setDirection(lookingVector.clone().multiply(1.5));
            Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), rocket::remove, 20 * 10);
        });
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
                    new GameDamageEvent((Player) rocket.getShooter(), entity,
                            GameplayConstants.ROCKET_DAMAGE, Weapon.ROCKET_LAUNCHER)
                            .fire();
                    Vector away = entity.getLocation().toVector().subtract(at.toVector()).normalize();
                    entity.setVelocity(away.multiply(1.5));
                });
    }
}
