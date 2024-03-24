package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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

    Location eyeLocation = player.getEyeLocation();
    Vector lookingVector = eyeLocation.getDirection();

    eyeLocation.getWorld().playSound(eyeLocation, SoundConstants.ROCKET_FIRE, .5f, 1f);
    player.launchProjectile(LargeFireball.class, lookingVector, rocket -> {
      rocket.setIsIncendiary(false);
      rocket.setYield(0f);
      rocket.setDirection(lookingVector.clone().multiply(2.4));
      Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), rocket::remove, 20 * 10L);
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

    at.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, at, 5, .75, .75, .75, 0);
    at.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, at, 5, .75, .75, .75, 0);
    at.getWorld().playSound(at, SoundConstants.ROCKET_DET, .75f, 1f);
    at.getNearbyLivingEntities(3, 3, 3)
        .forEach(entity -> {
          Location entityLoc = entity.getLocation();
          double damageFactor = 1d / (Math.max(1.5, entityLoc.distance(at)) - 0.5);
          if (entity == rocket.getShooter()) {
            damageFactor *= 0.75;
          }
          new GameDamageEvent((Player) rocket.getShooter(), entity,
              GameplayConstants.ROCKET_DAMAGE * damageFactor,
              Weapon.ROCKET_LAUNCHER)
              .fire();
          Vector away = entity.getLocation().toVector().subtract(at.toVector()).normalize();
          entity.setVelocity(away.multiply(1.5));
        });
  }
}
