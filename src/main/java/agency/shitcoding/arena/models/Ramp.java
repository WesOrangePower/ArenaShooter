package agency.shitcoding.arena.models;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.SoundConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class Ramp implements Cloneable {

  @Getter private final String id;
  @Getter private Location firstLocation;
  @Getter private Location secondLocation;
  @Getter private final boolean multiply;
  @Getter private Vector vector;

  public Ramp(String id, Location firstLocation, Location secondLocation, boolean multiply,
      Vector vector) {
    this.id = id;
    this.firstLocation = firstLocation;
    this.secondLocation = secondLocation;
    this.multiply = multiply;
    this.vector = vector;
  }

  private BoundingBox boundingBox;

  public BoundingBox getBoundingBox() {
    if (boundingBox == null) {
      boundingBox = BoundingBox.of(firstLocation.getBlock(), secondLocation.getBlock());
    }
    return boundingBox;
  }

  @SuppressWarnings("deprecation")
  public boolean isTouching(Player player) {
    boolean contains =
        getBoundingBox().contains(player.getLocation().toVector().subtract(new Vector(0, 1, 0)));
    return contains && player.isOnGround();
  }

  Set<Player> recentlyLaunched = new HashSet<>();

  public void apply(Player player) {
    if (recentlyLaunched.contains(player)) {
      return;
    }

    doApply(player);
    recentlyLaunched.add(player);
    Bukkit.getScheduler()
        .runTaskLater(ArenaShooter.getInstance(), () -> recentlyLaunched.remove(player), 3L);
  }

  public void doApply(Player player) {
    Location location = player.getLocation();
    location.getWorld().playSound(location, SoundConstants.PAD, SoundCategory.VOICE, .6f, 1f);
    Vector vectorToApply = multiply ? player.getVelocity().multiply(vector) : vector;
    player.setVelocity(vectorToApply);
  }

  @Override
  public Ramp clone() {
    try {
      Ramp clone = (Ramp) super.clone();
      clone.firstLocation = firstLocation.clone();
      clone.secondLocation = secondLocation.clone();
      clone.vector = vector.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
