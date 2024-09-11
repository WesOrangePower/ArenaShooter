package agency.shitcoding.arena.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class LootPoint implements Cloneable {

  private int id;
  private Location location;
  private Powerup type;

  @Override
  public LootPoint clone() {
    try {
      LootPoint clone = (LootPoint) super.clone();
      clone.location = location.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
