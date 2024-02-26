package agency.shitcoding.arena.events.listeners;

import static agency.shitcoding.arena.GameplayConstants.MACHINE_GUN_DAMAGE;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.Weapon;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


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

    fireMachineGun(player);
    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> fireMachineGun(player), 2);
  }

  private void fireMachineGun(Player player) {
    if (Ammo.getAmmoForPlayer(player, Ammo.BULLETS) <= 0) {
      return;
    }

    Location eyeLocation = player.getEyeLocation();
    Vector lookingVector = eyeLocation.getDirection();
    World world = eyeLocation.getWorld();

    world.playSound(eyeLocation, SoundConstants.MACHINE_FIRE, .5f, 2f);


    // row of particles
    int iterations = 0;
    for (int i = 0; i < SCAN_LEN; i++) {
      // for DENSITY_FACTOR times/block in the direction of the player's looking direction
      // spawn a particle

      for (int j = 0; j < DENSITY_FACTOR; j++) {
        iterations++;
        var at = eyeLocation.add(lookingVector.clone().normalize().multiply(i / DENSITY_FACTOR));


        if (iterations % 3 == 0) {
          world.spawnParticle(Particle.CRIT, at, 1, 0, 0, 0, 0);
        }

        if (at.getBlock().getType().isCollidable()) {
          // if the block is collidable, stop the loop
          return;
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
        affectedEntities.forEach(
            entity -> new GameDamageEvent(player, entity, MACHINE_GUN_DAMAGE, Weapon.MACHINE_GUN)
                .fire());
        if (!affectedEntities.isEmpty()) {
          return;
        }
      }
    }
  }
}
