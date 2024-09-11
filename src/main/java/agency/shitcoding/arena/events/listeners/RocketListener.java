package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.WeaponMods;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
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

    if (isKittyCannon(player)) {
      eyeLocation.getWorld().playSound(eyeLocation, Sound.ENTITY_CAT_DEATH, .5f, 1f);
      Vector velocity = lookingVector.multiply(2f);
      eyeLocation
          .getWorld()
          .spawnEntity(
              eyeLocation,
              EntityType.CAT,
              SpawnReason.COMMAND,
              entity -> {
                Cat cat = (Cat) entity;
                cat.setAI(false);
                cat.setVelocity(velocity);
                cat.setGravity(false);
                cat.setInvulnerable(true);
                player.launchProjectile(
                    SmallFireball.class,
                    lookingVector,
                    rocket -> {
                      rocket.addPassenger(cat);
                      rocket.setIsIncendiary(false);
                      rocket.setYield(0f);
                      rocket.setDirection(lookingVector.clone());
                      rocket.getVelocity().multiply(2f);
                      Bukkit.getScheduler()
                          .runTaskLater(
                              ArenaShooter.getInstance(),
                              () -> {
                                cat.remove();
                                rocket.remove();
                              },
                              20 * 10L);
                    });
              });
    } else {
      eyeLocation.getWorld().playSound(eyeLocation, SoundConstants.ROCKET_FIRE, .5f, 1f);
      player.launchProjectile(
          LargeFireball.class,
          lookingVector,
          rocket -> {
            rocket.setIsIncendiary(false);
            rocket.setYield(0f);
            rocket.setDirection(lookingVector.clone());
            rocket.getVelocity().multiply(2f);
            Bukkit.getScheduler()
                .runTaskLater(ArenaShooter.getInstance(), rocket::remove, 20 * 10L);
          });
    }
  }

  private static boolean isKittyCannon(Player player) {
      return WeaponMods.getKittyCannon()
              .equals(CosmeticsService.getInstance().getWeaponMod(player, Weapon.ROCKET_LAUNCHER));
  }

  @EventHandler
  public void onKittyCannonCollision(ProjectileHitEvent event) {
    if (!(event.getEntity() instanceof SmallFireball rocket)) {
      return;
    }

    var passengers = rocket.getPassengers();
    if (passengers.isEmpty()) {
      return;
    }

    event.setCancelled(true);
    var hitEntity = event.getHitEntity();
    if (hitEntity != null && hitEntity.isInvulnerable()) {
      return;
    }

    for (Entity passenger : passengers) {
      rocket.removePassenger(passenger);
      passenger.remove();
    }
    if (rocket.getShooter() instanceof Player player) {
      explodeCat(rocket.getLocation(), player);
    }
    rocket.remove();
  }

  @EventHandler
  public void onFireballCollision(ProjectileHitEvent event) {
    if (!(event.getEntity() instanceof LargeFireball rocket)) {
      return;
    }
    event.setCancelled(true);

    Location at = rocket.getLocation();
    if (rocket.getShooter() instanceof Player player) {
      explode(at, player);
    }
    rocket.remove();
  }

  private void explodeCat(Location at, Player shooter) {
    explode(at, shooter, new Particle[]{ Particle.HEART, Particle.EXPLOSION_HUGE });
  }

  private void explode(Location at, Player shooter) {
    explode(at, shooter, new Particle[] {Particle.EXPLOSION_NORMAL, Particle.EXPLOSION_HUGE});
  }

  private void explode(Location at, Player shooter, Particle[] particles) {
    for (Particle particle : particles) {
      at.getWorld().spawnParticle(particle, at, 5, .75, .75, .75, 0);
    }
    at.getWorld().playSound(at, SoundConstants.ROCKET_DET, .75f, 1f);
    at.getNearbyLivingEntities(3, 3, 3)
        .forEach(
            entity -> {
              Location entityLoc = entity.getLocation();
              double damageFactor = 1d / (Math.max(1.5, entityLoc.distance(at)) - 0.5);
              if (entity == shooter) {
                damageFactor *= 0.75;
              }
              if (entity.getType() == EntityType.PLAYER
                  && GameOrchestrator.getInstance()
                      .getGameByPlayer((Player) entity)
                      .map(game -> game.getRuleSet() == RuleSet.ROF).orElse(false)) {

                Vector away = entity.getLocation().toVector().subtract(at.toVector()).normalize();
                entity.setVelocity(away.multiply(1.5));

                return;
              }

              new GameDamageEvent(
                      shooter,
                      entity,
                      GameplayConstants.ROCKET_DAMAGE * damageFactor,
                      Weapon.ROCKET_LAUNCHER)
                  .fire();
              Vector away = entity.getLocation().toVector().subtract(at.toVector()).normalize();
              entity.setVelocity(away.multiply(1.5));
            });
  }
}
