package agency.shitcoding.arena.models.door;

import java.util.List;

import agency.shitcoding.arena.storage.framework.ConfigurationMappable;
import agency.shitcoding.arena.storage.framework.annotation.MappedField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoorTrigger implements Cloneable, ConfigurationMappable {
  private String id;

  @MappedField("door_ids")
  private List<String> doorIds;

  @MappedField("trigger_type")
  private int triggerType;

  @MappedField private Location location;

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
  public static final int PROXIMITY = 1 << 1;
  //  public static final int SHOOT = 1 << 2; TODO
}
