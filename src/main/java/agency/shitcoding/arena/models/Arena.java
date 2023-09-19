package agency.shitcoding.doublejump.models;

import agency.shitcoding.doublejump.models.LootPoint;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Set;

@Getter
@Setter
public class Arena {
    String name;
    Location lowerBound;
    Location upperBound;
    Set<LootPoint> lootPoints;
}
