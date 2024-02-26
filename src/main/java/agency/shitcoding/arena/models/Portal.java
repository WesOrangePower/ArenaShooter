package agency.shitcoding.arena.models;

import agency.shitcoding.arena.events.GameDamageEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

@RequiredArgsConstructor
public class Portal {

  @Getter
  private final String id;
  @Getter
  private final Location firstLocation;
  @Getter
  private final Location secondLocation;
  @Getter
  private final Location targetLocation;

  private Block lowerBlock = null;
  private Block upperBlock = null;

  public Block getLowerBlock() {
    if (lowerBlock == null) {
      int minX = Math.min(firstLocation.getBlockX(), secondLocation.getBlockX());
      int minY = Math.min(firstLocation.getBlockY(), secondLocation.getBlockY());
      int minZ = Math.min(firstLocation.getBlockZ(), secondLocation.getBlockZ());
      Location location = new Location(firstLocation.getWorld(), minX, minY, minZ);
      lowerBlock = location.getBlock();
    }
    return lowerBlock;
  }

  public Block getUpperBlock() {
    if (upperBlock == null) {
      int maxX = Math.max(firstLocation.getBlockX(), secondLocation.getBlockX());
      int maxY = Math.max(firstLocation.getBlockY(), secondLocation.getBlockY());
      int maxZ = Math.max(firstLocation.getBlockZ(), secondLocation.getBlockZ());
      Location location = new Location(firstLocation.getWorld(), maxX, maxY, maxZ);
      upperBlock = location.getBlock();
    }
    return upperBlock;
  }

  public BoundingBox getBoundingBox() {
    return BoundingBox.of(getLowerBlock(), getUpperBlock());
  }

  public void teleport(Player player) {
    Block block = targetLocation.getBlock();
    Block blockAbove = block.getRelative(0, 1, 0);
    BoundingBox boundingBox = BoundingBox.of(block, blockAbove).expand(1.2);
    targetLocation.getNearbyLivingEntities(1).stream()
        .filter(Player.class::isInstance)
        .map(Player.class::cast)
        .filter(entity -> !entity.equals(player))
        .filter(entity -> boundingBox.contains(entity.getLocation().toCenterLocation().toVector()))
        .findFirst()
        .ifPresent(entity -> new GameDamageEvent(player, entity, 1000, Weapon.GAUNTLET).fire());
    player.teleport(targetLocation);
    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, .2f, 1.3f);
    player.spawnParticle(Particle.PORTAL, player.getLocation(), 20, 1, .2, 1, 2);
  }
}
