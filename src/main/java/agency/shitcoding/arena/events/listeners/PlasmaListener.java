package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.gamestate.WeaponMods;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;


public class PlasmaListener implements Listener {

  private static final Material PLASMA = Weapon.PLASMA_GUN.item;
  private final Random random = new Random();

  @EventHandler
  public void onPlayerInteract(GameShootEvent event) {
    Player player = event.getParentEvent().getPlayer();
    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().isAir()
        || itemInMainHand.getType() != PLASMA
        || player.getCooldown(PLASMA) > 0) {
      return;
    }
    Weapon.applyCooldown(player, Weapon.PLASMA_GUN.cooldown);
    World world = player.getLocation().getWorld();

    world.playSound(player, SoundConstants.HITSOUND, .75f, 1f);

    fireSnowBall(player);
    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> fireSnowBall(player), 1);
    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> fireSnowBall(player), 2);
    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> fireSnowBall(player), 3);
    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> fireSnowBall(player), 4);
  }

  private void fireSnowBall(Player player) {
    var lookingVector = player.getEyeLocation().getDirection().clone();
    lookingVector.add(
        new Vector(
            random.nextFloat(.04f) - .02f,
            random.nextFloat(.04f) - .02f,
            random.nextFloat(.04f) - .02f
        )
    );
    ItemStack hand = player.getInventory().getItemInMainHand();
    if (hand.getType() == PLASMA
        && player.getCooldown(PLASMA) > 0
        && player.getGameMode() == GameMode.ADVENTURE) {

      var isSlimaGun = isSlimaGun(player);
      var sound = isSlimaGun ? Registry.SOUNDS.getKeyOrThrow(Sound.ENTITY_SLIME_JUMP_SMALL).asString()
          : SoundConstants.PLASMA_FIRE;

      player.getLocation().getWorld()
          .playSound(player.getEyeLocation(), sound, .75f, 1f);

      player.launchProjectile(Snowball.class, lookingVector, projectile -> {
        if (isSlimaGun) {
          projectile.setItem(new ItemStack(Material.SLIME_BALL));
        }
        projectile.setGravity(false);
        projectile.setVelocity(lookingVector.clone().multiply(1.5));
        Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), projectile::remove, 20L * 5);
      });
    }
  }


  @EventHandler
  public void onSnowBallCollision(ProjectileHitEvent event) {
    if (!(event.getEntity() instanceof Snowball snowball)) {
      return;
    }
    var hitEntity = event.getHitEntity();
    if (IgnoreEntities.shouldIgnoreEntity(hitEntity)) {
      return;
    }

    event.setCancelled(true);
    Location at = snowball.getLocation();
    at.getWorld().playSound(at, SoundConstants.PLASMA_HIT, .5f, 1f);

    if (hitEntity == null) {
      if (snowball.getShooter() instanceof Player player && isSlimaGun(player)) {
        at.getWorld().spawnParticle(Particle.ITEM_SLIME, at, 5, 1.5, 1.5, 1.5, 0);
      } else {
        at.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, at, 5, 1.5, 1.5, 1.5, 0);
      }
      for (LivingEntity nearbyEntity : at.getNearbyLivingEntities(.2, .2, .2)) {
        if (nearbyEntity instanceof Player player && (player.getGameMode() == GameMode.ADVENTURE)) {
            new GameDamageEvent(player, nearbyEntity, 1.3, Weapon.PLASMA_GUN).fire();
        }
      }
      return;
    }

    if (hitEntity instanceof Player player &&
        (player.getGameMode() == GameMode.ADVENTURE &&
            (snowball.getShooter() instanceof Player shooter))) {
      shooter.playSound(shooter, SoundConstants.HITSOUND, .75f, 1f);
      new GameDamageEvent(shooter, player, 2, Weapon.PLASMA_GUN)
          .fire();
    }

    snowball.remove();
  }

  private static boolean isSlimaGun(Player player) {
    return WeaponMods.getSlimaGun()
        .equals(CosmeticsService.getInstance().getWeaponMod(player, Weapon.PLASMA_GUN));
  }
}
