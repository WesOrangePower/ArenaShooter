package agency.shitcoding.arena.models;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class WindTunnel implements Cloneable {
  @Getter private String id;
  @Getter private Location firstCorner;
  @Getter private Location secondCorner;
  @Getter private Vector velocity;

  public WindTunnel(String id, Location firstCorner, Location secondCorner, Vector velocity) {
    this.id = id;
    this.firstCorner = firstCorner;
    this.secondCorner = secondCorner;
    this.velocity = velocity;
  }

  private transient BoundingBox boundingBox = null;

  public BoundingBox getBoundingBox() {
    if (boundingBox == null) {
      boundingBox = BoundingBox.of(firstCorner.getBlock(), secondCorner.getBlock());
    }
    return boundingBox;
  }

  public boolean isInside(Player player) {
    return getBoundingBox().contains(player.getLocation().toVector());
  }

  @Override
  public WindTunnel clone() {
    try {
      WindTunnel clone = (WindTunnel) super.clone();
      clone.firstCorner = firstCorner.clone();
      clone.secondCorner = secondCorner.clone();
      clone.velocity = velocity.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
