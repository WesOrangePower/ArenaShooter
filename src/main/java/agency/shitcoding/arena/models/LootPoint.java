package agency.shitcoding.arena.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@Data
@RequiredArgsConstructor
public class LootPoint {

  private final int id;
  private final Location location;
  private final Powerup type;
}
