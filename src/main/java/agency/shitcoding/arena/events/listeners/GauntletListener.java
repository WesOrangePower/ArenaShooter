package agency.shitcoding.arena.events.listeners;

import static agency.shitcoding.arena.GameplayConstants.GAUNTLET_DAMAGE;

import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.models.Weapon;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GauntletListener implements Listener {

  private static final Material GAUNTLET = Weapon.GAUNTLET.item;

  public static boolean bloodParticles = true;
  private final Random random = new Random();

  @EventHandler
  public void onPlayerInteract(GameShootEvent event) {
    Player player = event.getParentEvent().getPlayer();
    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().isAir()
        || itemInMainHand.getType() != GAUNTLET
        || player.getCooldown(GAUNTLET) > 0) {
      return;
    }
    Weapon.applyCooldown(player, Weapon.GAUNTLET.cooldown);
    Location eyeLocation = player.getEyeLocation();
    Vector lookingVector = eyeLocation.getDirection();
    World world = eyeLocation.getWorld();

    // row of particles
    var at = eyeLocation.add(lookingVector.clone().normalize());
    world.spawnParticle(Particle.ELECTRIC_SPARK, at, 1, 0, 0, 0, 0);

    Set<LivingEntity> affectedEntities = new HashSet<>();
    at.getWorld().getNearbyLivingEntities(at, 1, 1, 1).stream()
        .filter(e -> !IgnoreEntities.shouldIgnoreEntity(e))
        .filter(entity -> entity != player)
        .forEach(
            entity -> {
              affectedEntities.add(entity);
              world.spawnParticle(Particle.ELECTRIC_SPARK, at, 1, 0, 0, 0);
              world.playSound(at, Sound.ENTITY_GUARDIAN_ATTACK, 1f, 1f);
              if (bloodParticles) {
                sprayBlood(at, entity);
              }
            });
    affectedEntities.forEach(
        entity -> new GameDamageEvent(player, entity, GAUNTLET_DAMAGE, Weapon.GAUNTLET).fire());
  }

  private void sprayBlood(Location at, Entity entity) {
    var entityCenter = entity.getLocation().add(0, entity.getHeight() / 2, 0);
    var direction = entityCenter.toVector().subtract(at.toVector()).normalize();
    var spray = direction.clone().multiply(0.5);
    for (int i = 0; i < 10; i++) {
      var offset = spray.clone().multiply(i);
      at.getWorld()
          .spawnParticle(
              Particle.DUST,
              at.clone().add(offset),
              1,
              random.nextDouble(.5),
              random.nextDouble(.5),
              random.nextDouble(.5),
              0,
              new Particle.DustOptions(Color.fromRGB(0x770000), 2f));
    }
  }
}
