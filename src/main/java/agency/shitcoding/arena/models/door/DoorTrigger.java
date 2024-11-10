package agency.shitcoding.arena.models.door;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoorTrigger implements Cloneable {
  private String triggerId;
  private List<String> doorIds;
  private int triggerType;
  private Location location;

  public DoorTrigger clone() {
    try {
      DoorTrigger doorTrigger = (DoorTrigger) super.clone();
      doorTrigger.location = location.clone();
      return doorTrigger;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public static final int INTERACTION = 1;
}
