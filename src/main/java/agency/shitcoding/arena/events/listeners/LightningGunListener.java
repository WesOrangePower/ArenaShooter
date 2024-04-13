package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.gamestate.Laser.GuardianLaser;
import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.Weapon;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;


public class LightningGunListener implements Listener {

  static final Set<Material> PASSABLE_MATERIALS = Arrays.stream(Material.values())
      .filter(material -> !material.isSolid())
      .collect(Collectors.toSet());

  static final ConcurrentHashMap<String, GuardianLaser> lasers = new ConcurrentHashMap<>();
  static final ConcurrentHashMap<String, Long> lastFired = new ConcurrentHashMap<>();

  public static final int SCAN_LEN = 16;
  private static final Material LIGHTNING = Weapon.LIGHTNING_GUN.item;

  @EventHandler
  public void onPlayerInteract(GameShootEvent event) {
    Player player = event.getParentEvent().getPlayer();
    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().isAir()
        || itemInMainHand.getType() != LIGHTNING
        || player.getCooldown(LIGHTNING) > 0) {
        return;
    }

    Weapon.applyCooldown(player, Weapon.LIGHTNING_GUN.cooldown);


    fireGun(player);
    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> fireGun(player), 1);
    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> fireGun(player), 2);
    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> fireGun(player), 3);
    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> fireGun(player), 4);
  }

  private void fireGun(Player player) {
    if (Ammo.getAmmoForPlayer(player, Ammo.LIGHTNING) <= 0) {
      return;
    }

    var name = player.getName();
    lastFired.put(name, System.currentTimeMillis());

    Location eyeLocation = player.getEyeLocation();
    World world = eyeLocation.getWorld();

    var los = player.getLineOfSight(PASSABLE_MATERIALS, SCAN_LEN);
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    LivingEntity target = los.subList(1, los.size() - 1).stream()
        .map(block -> world.getNearbyLivingEntities(block.getLocation(), 1, 1, 1))
        .filter(foundEntities -> !foundEntities.isEmpty()
            && foundEntities.stream().anyMatch(entity -> !entity.getName().equals(player.getName())))
        .findFirst()
        .map(foundEntities -> foundEntities.stream().findFirst().get()).orElse(null);

    var finalLos = los.get(los.size() - 1);
    var end = target != null ? target.getEyeLocation() : finalLos.getLocation();
    GuardianLaser laser = lasers.computeIfAbsent(name, s -> {
      try {
        GuardianLaser l = target != null
            ? new GuardianLaser(eyeLocation, target, -1, 64)
            : new GuardianLaser(eyeLocation, end, -1, 64);
        Bukkit.getScheduler().runTaskTimer(ArenaShooter.getInstance(), task -> {
          var lf = lastFired.get(name);
          if (lf == null || System.currentTimeMillis() - lf > 300) {
            l.stop();
            lasers.remove(name);
            lastFired.remove(name);
            task.cancel();
          }
        }, 0, 1);

        AtomicBoolean fullSecond = new AtomicBoolean(true);
        Bukkit.getScheduler().runTaskTimer(ArenaShooter.getInstance(), task -> {
          var lf = lastFired.get(name);
          if (lf == null || System.currentTimeMillis() - lf > 300) {
            task.cancel();
          }
          if (fullSecond.getAndSet(!fullSecond.get())) {
            world.playSound(eyeLocation, SoundConstants.LG_FIRE, SoundCategory.VOICE, .75f, 1f);
          }
        }, 0, 9);
        l.start(ArenaShooter.getInstance());
        return l;
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    });

    try {
      laser.moveStart(eyeLocation);
      laser.moveEnd(target != null ? target.getEyeLocation() : end);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }

    if (target != null) {
      var originalVelocity = player.getVelocity();
      new GameDamageEvent(player, target, GameplayConstants.LIGHTNING_DAMAGE, Weapon.LIGHTNING_GUN).fire();
      Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> player.setVelocity(originalVelocity), 1);
    }
  }
}
