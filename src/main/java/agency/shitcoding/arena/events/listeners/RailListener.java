package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.events.GameStreakUpdateEvent;
import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.PlayerScore;
import agency.shitcoding.arena.gamestate.WeaponMods;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class RailListener implements Listener {

  public static final int DENSITY_FACTOR = 5;
  public static final int SCAN_LEN = 32;
  private static final Material RAILGUN = Weapon.RAILGUN.item;

  @EventHandler
  public void onPlayerInteract(GameShootEvent event) {
    Player player = event.getParentEvent().getPlayer();
    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().isAir()
        || itemInMainHand.getType() != RAILGUN
        || player.getCooldown(RAILGUN) > 0) {
      return;
    }
    Weapon.applyCooldown(player, Weapon.RAILGUN.cooldown);
    Location eyeLocation = player.getEyeLocation();
    Vector lookingVector = eyeLocation.getDirection();
    World world = eyeLocation.getWorld();
    boolean isBubbleGun = isBubbleGun(player);
    var particle = isBubbleGun ? Particle.WATER_BUBBLE : Particle.WAX_OFF;
    var sound = isBubbleGun ? Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE.key().value()
        : SoundConstants.RAIL_FIRE;

    world.playSound(player, sound, .75f, 1f);
    List<Location> particleLocations = new ArrayList<>();

    Set<LivingEntity> affectedEntities = new HashSet<>();
    // row of particles
    outer:
    for (int i = 0; i < SCAN_LEN; i++) {
      // for DENSITY_FACTOR times/block in the direction of the player's looking direction
      // spawn a particle

      for (int j = 0; j < DENSITY_FACTOR; j++) {
        var at = eyeLocation.add(lookingVector.clone().normalize().multiply(i / DENSITY_FACTOR));
        particleLocations.add(at.clone());

        if (at.getBlock().getType().isCollidable()) {
          // if the block is collidable, stop the loop
          break outer;
        }

        at.getWorld().getNearbyEntities(at, .2, .2, .2)
            .stream()
            .filter(LivingEntity.class::isInstance)
            .filter(entity -> entity != player)
            .forEach(entity -> {
              affectedEntities.add((LivingEntity) entity);
              world.spawnParticle(Particle.FLASH, at, 10, .2, .2, .2, 0);
            });
      }
    }

    particleLocations.forEach(loc -> world.spawnParticle(particle, loc, 1, 0, 0, 0, 0));
    if (isBubbleGun) {
      for(int i = 1; i < 20; i++) {
        Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(),
            () -> particleLocations.forEach(loc -> world.spawnParticle(particle, loc, 1, 0, 0, 0, 0)),
            i
        );
      }
    }

    affectedEntities.forEach(entity ->
        new GameDamageEvent(player, entity, GameplayConstants.RAILGUN_DAMAGE, Weapon.RAILGUN).fire()
    );
    var optGame = GameOrchestrator.getInstance().getGameByPlayer(player);
    if (optGame.isEmpty()) return;
    var game = optGame.get();
    var optStreak =
        Optional.ofNullable(game.getScore(player))
            .map(PlayerScore::getStreak);
    if (optStreak.isEmpty()) return;
    var streak = optStreak.get();
    var oldStreak = streak.copy();

    if (affectedEntities.isEmpty()) {
      streak.setConsequentRailHit(0);
    } else {
      streak.setConsequentRailHit(streak.getConsequentRailHit() + 1);
    }
    new GameStreakUpdateEvent(streak, oldStreak, player, game)
        .fire();
  }

  private static boolean isBubbleGun(Player player) {
    return WeaponMods.getBubbleGun()
            .equals(CosmeticsService.getInstance().getWeaponMod(player, Weapon.RAILGUN));
  }
}
