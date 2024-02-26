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

import static agency.shitcoding.arena.GameplayConstants.GAUNTLET_DAMAGE;


public class GauntletListener implements Listener {

  private static final Material GAUNTLET = Weapon.GAUNTLET.item;

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
    at.getWorld().getNearbyEntities(at, 1, 1, 1)
        .stream()
        .filter(entity -> entity instanceof LivingEntity)
        .filter(entity -> entity != player)
        .forEach(entity -> {
          affectedEntities.add((LivingEntity) entity);
          world.spawnParticle(Particle.ELECTRIC_SPARK, at, 1, 0, 0, 0);
          world.playSound(at, Sound.ENTITY_GUARDIAN_ATTACK, 1f, 1f);
        });
    affectedEntities.forEach(
        entity -> new GameDamageEvent(player, entity, GAUNTLET_DAMAGE, Weapon.GAUNTLET)
            .fire());
  }
}
