package agency.shitcoding.arena.models;

import agency.shitcoding.arena.storage.framework.ConfigurationMappable;
import agency.shitcoding.arena.storage.framework.annotation.MappedField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoredHologram implements Cloneable, ConfigurationMappable {
  private String id;
  @MappedField
  private String text;
  @MappedField
  private Location location;
  @MappedField
  private int lifetime;
  @MappedField
  @Nullable
  private String command;

  @Override
  public StoredHologram clone() {
    try {
      StoredHologram storedHologram = (StoredHologram) super.clone();
      storedHologram.id = id;
      storedHologram.text = text;
      storedHologram.location = location.clone();
      storedHologram.lifetime = lifetime;
      return storedHologram;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
}
