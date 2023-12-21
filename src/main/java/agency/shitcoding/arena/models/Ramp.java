package agency.shitcoding.arena.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class Ramp {
    @Getter
    private final String id;
    @Getter
    private final Location firstLocation;
    @Getter
    private final Location secondLocation;
    @Getter
    private final boolean multiply;
    @Getter
    private final Vector vector;

    private BoundingBox boundingBox;

    public BoundingBox getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = BoundingBox.of(firstLocation.getBlock(), secondLocation.getBlock());
        }
        return boundingBox;
    }

    @SuppressWarnings("deprecation")
    public boolean isTouching(Player player) {
        boolean contains = getBoundingBox().contains(player.getLocation().toVector()
                .subtract(new Vector(0, 1, 0)));
        return contains && player.isOnGround();
    }

    public void apply(Player player) {
        Vector vectorToApply = multiply ? player.getVelocity().multiply(vector) : vector;
        player.setVelocity(vectorToApply);
    }

}
