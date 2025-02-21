package agency.shitcoding.arena.models;

import agency.shitcoding.arena.storage.framework.ConfigurationMappable;
import agency.shitcoding.arena.storage.framework.annotation.MappedField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class WindTunnel implements Cloneable, ConfigurationMappable {
  private String id;
  @MappedField("first_corner")
  private Location firstCorner;
  @MappedField("second_corner")
  private Location secondCorner;
  @MappedField
  private Vector velocity;

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

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;

    WindTunnel that = (WindTunnel) o;
    return Objects.equals(getId(), that.getId()) && Objects.equals(getFirstCorner(), that.getFirstCorner()) && Objects.equals(getSecondCorner(), that.getSecondCorner()) && Objects.equals(getVelocity(), that.getVelocity());
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(getId());
    result = 31 * result + Objects.hashCode(getFirstCorner());
    result = 31 * result + Objects.hashCode(getSecondCorner());
    result = 31 * result + Objects.hashCode(getVelocity());
    return result;
  }
}
