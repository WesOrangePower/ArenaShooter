package agency.shitcoding.arena.models;

import agency.shitcoding.arena.storage.framework.ConfigurationMappable;
import agency.shitcoding.arena.storage.framework.annotation.MappedField;
import lombok.*;
import org.bukkit.Location;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LootPoint implements Cloneable, ConfigurationMappable {

  private String id;
  @MappedField private Location location;
  @MappedField("is_spawn_point") private boolean isSpawnPoint;
  @MappedField private Powerup type;
  @MappedField private int markers;

  @Override
  public LootPoint clone() {
    try {
      LootPoint clone = (LootPoint) super.clone();
      clone.location = location.clone();
      clone.isSpawnPoint = isSpawnPoint;
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;

    LootPoint lootPoint = (LootPoint) o;
    return isSpawnPoint() == lootPoint.isSpawnPoint()
        && getMarkers() == lootPoint.getMarkers()
        && getId().equals(lootPoint.getId())
        && getLocation().equals(lootPoint.getLocation())
        && getType() == lootPoint.getType();
  }

  @Override
  public int hashCode() {
    int result = getId().hashCode();
    result = 31 * result + getLocation().hashCode();
    result = 31 * result + Boolean.hashCode(isSpawnPoint());
    result = 31 * result + getType().hashCode();
    result = 31 * result + getMarkers();
    return result;
  }
}
