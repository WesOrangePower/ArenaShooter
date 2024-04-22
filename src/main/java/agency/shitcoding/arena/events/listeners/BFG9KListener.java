package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.AmmoUpdateEvent;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.models.Weapon;
import com.destroystokyo.paper.event.entity.EnderDragonFireballHitEvent;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BFG9KListener implements Listener {

  public static final Material BFG = Weapon.BFG9K.item;

  @EventHandler
  public void onShooting(GameShootEvent event) {
    Player player = event.getParentEvent().getPlayer();
    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().isAir()
        || itemInMainHand.getType() != BFG
        || player.getCooldown(BFG) > 0) {
      return;
    }

    Weapon.applyCooldown(player, Weapon.BFG9K.cooldown);

    Location eyeLocation = player.getEyeLocation();
    Vector lookingVector = eyeLocation.getDirection();

    eyeLocation.getWorld().playSound(eyeLocation,
        SoundConstants.BFG_FIRE,
        .5f,
        1f);

    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(),
        () -> fireBFG(player, lookingVector),
        10);

  }

  private void fireBFG(Player player, Vector lookingVector) {
    if (player.getInventory().getItemInMainHand().getType().equals(BFG))
      player.launchProjectile(DragonFireball.class, lookingVector, proj -> {
        proj.setIsIncendiary(false);
        proj.setYield(0f);
        proj.setDirection(lookingVector.clone());
        Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), proj::remove, 20 * 5L);
      });
    else {
      // return ammo
      new AmmoUpdateEvent(player, Weapon.BFG9K.ammoPerShot, Weapon.BFG9K.ammo)
          .fire();
    }
  }

  @EventHandler
  public void onCollision(EnderDragonFireballHitEvent event) {
    DragonFireball proj = event.getEntity();

    event.setCancelled(true);

    Location at = proj.getLocation();
    World w = at.getWorld();
    Player shooter = (Player) proj.getShooter();
    if (shooter == null) {
      return;
    }

    proj.remove();

    w.getNearbyLivingEntities(at, 1, 1, 1)
        .stream()
        .filter(le -> !le.equals(shooter))
        .forEach(hit -> {
          Location loc = hit.getLocation();
          new GameDamageEvent(shooter, hit,
              GameplayConstants.BFG_DAMAGE, Weapon.BFG9K)
              .fire();
          Vector away = loc.toVector().subtract(at.toVector()).normalize();
          away.setY(0.5);
          away.multiply(1.5);
          hit.setVelocity(away);
        });

    w.spawnParticle(Particle.PORTAL, at, 5, .75, .75, .75, 2);
    w.spawnParticle(Particle.SNOWBALL, at, 20, 3, 3, 3, 0);
    boom(at, Material.EMERALD_BLOCK.createBlockData(), SoundConstants.BFG_HIT);

    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> bfgSpray(shooter, at), 30);
  }

  private void bfgSpray(Player shooter, Location projLoc) {

    if (shooter.getGameMode() != GameMode.ADVENTURE) {
      return;
    }

    BlockData blockData = Material.CRYING_OBSIDIAN.createBlockData();
    boom(projLoc, blockData, SoundConstants.ROCKET_DET);

    projLoc.getNearbyLivingEntities(40d, 20d, le -> !le.equals(shooter))
        .forEach(hit -> {
          Location loc = hit.getEyeLocation();
          World w = loc.getWorld();

          var to1 = loc.toVector().subtract(projLoc.toVector()).normalize();
          var rt1 = w.rayTrace(
              projLoc,
              to1,
              30,
              FluidCollisionMode.NEVER, // ignore fluid
              true, // ignore passable
              1,
              en -> en.equals(hit)
          );
          if (rt1 == null) {
            return;
          }
          var shooterLoc = shooter.getEyeLocation();
          var to2 = loc.toVector().subtract(shooterLoc.toVector()).normalize();
          var rt2 = w.rayTrace(
              shooterLoc,
              to2,
              30,
              FluidCollisionMode.NEVER, // ignore fluid
              true, // ignore passable
              1,
              en -> en.equals(hit)
          );

          if (rt2 == null) {
            return;
          }

          if (rt1.getHitEntity() == hit && rt2.getHitEntity() == hit) {
            w.spawnParticle(Particle.BLOCK_CRACK, loc,
                5, .75, .75, .75,
                blockData);
            w.spawnParticle(Particle.BLOCK_CRACK, hit.getLocation(),
                5, .75, .75, .75,
                blockData);
            double distanceFromProj = projLoc.distance(hit.getLocation());
            double damageFactor = 1d / (Math.max(1.5, Math.min(distanceFromProj, .01)) - .5);
            double damage = GameplayConstants.BFG_SPREAD_DAMAGE * damageFactor;
            new GameDamageEvent(shooter,
                hit,
                damage,
                Weapon.BFG9K)
                .fire();
          }
        });
  }

  private void boom(Location at, BlockData blockData, String sound) {
    double x = at.getX(), y = at.getY(), z = at.getZ();
    World w = at.getWorld();
    w.playSound(at, sound, .75f, 1);
    for (int dx = -2; dx < 2; dx++) {
      for (int dy = -2; dy < 2; dy++) {
        for (int dz = -2; dz < 2; dz++) {
          Location loc = new Location(w, x + dx, y + dy, z + dz);
          w.spawnParticle(Particle.BLOCK_CRACK, loc, 5, .75, .75, .75,
              blockData
          );
        }
      }
    }
  }

}
