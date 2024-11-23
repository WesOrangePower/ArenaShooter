package agency.shitcoding.arena.laser;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Squid;

import static agency.shitcoding.arena.events.listeners.IgnoreEntities.ignoreEntity;

public class GuardianLaser implements AutoCloseable {

  LivingEntity target;
  Guardian guardian;

  public GuardianLaser(Location start, Location end) {
    this(start, spawnSquid(end));
  }

  public GuardianLaser(Location start, LivingEntity endEntity) {
    this.target = endEntity;
    spawnGuardian(start);

    guardian.setTarget(target);
  }

  public void setTarget(LivingEntity target) {
    if (target.getType() == EntityType.SQUID) {
      target.remove();
    }
    this.target = target;
    guardian.setTarget(target);
  }

  public void setTarget(Location targetLocation) {
    if (target.getType() == EntityType.SQUID) {
      target.teleport(targetLocation);
      return;
    }

    target = spawnSquid(targetLocation);
    guardian.setTarget(target);
  }

  public void setStart(Location start) {
    guardian.teleport(start);
  }

  public void startBeam() {
    guardian.setLaser(true);
  }

  public void stopBeam() {
    guardian.setLaser(false);
  }

  private static Squid spawnSquid(Location location) {
    var squid = location.getWorld().spawn(location, Squid.class);
    squid.setInvisible(true);
    squid.setGravity(false);
    squid.setAI(false);
    squid.setInvulnerable(true);
    squid.setSilent(true);
    ignoreEntity(squid::getPersistentDataContainer);
    return squid;
  }

  private void spawnGuardian(Location location) {
    guardian = target.getWorld().spawn(location, Guardian.class);
    guardian.setInvisible(true);
    guardian.setGravity(false);
    guardian.setAI(false);
    guardian.setInvulnerable(true);
    ignoreEntity(guardian::getPersistentDataContainer);
    guardian.setSilent(true);
  }

  @Override
  public void close() {
    guardian.remove();

    if (target.getType() == EntityType.SQUID) {
      target.remove();
    }
  }
}
