package agency.shitcoding.arena.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class LootPoint implements Cloneable {

  private int id;
  private Location location;
  private boolean isSpawnPoint;
  private Powerup type;
  @Setter
  private int markers;

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
}
