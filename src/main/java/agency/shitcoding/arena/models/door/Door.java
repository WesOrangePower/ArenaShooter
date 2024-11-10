package agency.shitcoding.arena.models.door;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.SoundConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

@Data
@NoArgsConstructor
public class Door implements Cloneable {
  private String doorId;
  private int doorType;
  private int animationTime;
  private int closeAfterTicks = -1;
  private boolean replaceAir = false;
  private Location edge1;
  private Location edge2;
  private Location destinationCenter;

  private boolean open = false;
  BlockDisplay[] blockDisplays;

  public Door(
      String doorId,
      int doorType,
      int animationTime,
      int closeAfterTicks,
      boolean replaceAir,
      Location edge1,
      Location edge2,
      Location destinationCenter) {
    this.doorId = doorId;
    this.doorType = doorType;
    this.animationTime = animationTime;
    this.closeAfterTicks = closeAfterTicks;
    this.replaceAir = replaceAir;
    this.edge1 = edge1;
    this.edge2 = edge2;
    this.destinationCenter = destinationCenter;
  }
  private Location getMinimalLocation(Location loc1, Location loc2) {
    return new Location(
        loc1.getWorld(),
        Math.min(loc1.getX(), loc2.getX()),
        Math.min(loc1.getY(), loc2.getY()),
        Math.min(loc1.getZ(), loc2.getZ()));
  }

  private Location getMaximalLocation(Location loc1, Location loc2) {
    return new Location(
        loc1.getWorld(),
        Math.max(loc1.getX(), loc2.getX()),
        Math.max(loc1.getY(), loc2.getY()),
        Math.max(loc1.getZ(), loc2.getZ()));
  }

  private Location getCenter(Location loc1, Location loc2) {
    return new Location(
        loc1.getWorld(),
        (loc1.getX() + loc2.getX()) / 2 + .5,
        (loc1.getY() + loc2.getY()) / 2,
        (loc1.getZ() + loc2.getZ()) / 2 + .5);
  }

  private int blockSum(Location min, Location max) {
    return (max.getBlockX() - min.getBlockX() + 1)
        * (max.getBlockY() - min.getBlockY() + 1)
        * (max.getBlockZ() - min.getBlockZ() + 1);
  }

  private BlockDisplay blockDisplayFrom(Block block) {
    var spawningLoc = block.getLocation();
    BlockDisplay display = block.getWorld().spawn(spawningLoc, BlockDisplay.class);
    display.setBlock(block.getBlockData());
    display.setGravity(false);
    display.setNoPhysics(true);
    return display;
  }

  private void startOpen() {
    Location min = getMinimalLocation(edge1, edge2);
    Location max = getMaximalLocation(edge1, edge2);
    World world = edge1.getWorld();
    Location sourceCenter = getCenter(min, max);

    Vector delta = deltaVector(sourceCenter, destinationCenter);

    int blockSum = blockSum(min, max);

    this.blockDisplays = new BlockDisplay[blockSum];
    int i = 0;

    for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
      for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
        for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
          Block block = world.getBlockAt(x, y, z);

          BlockDisplay display = blockDisplayFrom(block);
          block.setType(Material.AIR);

          display.setInterpolationDuration(animationTime);
          display.setInterpolationDelay(1);
          Transformation transformation = display.getTransformation();
          transformation.getTranslation().set(delta.toVector3d());
          display.setTransformation(transformation);

          blockDisplays[i++] = display;
        }
      }
    }

    Bukkit.getScheduler()
        .runTaskLater(
            ArenaShooter.getInstance(),
            () -> {
              for (BlockDisplay display : blockDisplays) {
                //noinspection DataFlowIssue
                Location blockDestination = display.getOrigin().clone().add(delta);
                display.teleport(blockDestination);
                display.setInterpolationDuration(0);
                display.setInterpolationDelay(-1);

                if (replaceAir) {
                  Block destinationBlock = blockDestination.getBlock();
                  if (destinationBlock.getType().isAir()) {
                    destinationBlock.setType(display.getBlock().getMaterial());
                    destinationBlock.setBlockData(display.getBlock());
                  }
                }
              }
            },
            animationTime);
  }

  public void open() {
    if (this.open) {
      return;
    }
    this.open = true;

    Bukkit.getScheduler()
        .runTask(
            ArenaShooter.getInstance(),
            this::startOpen
        );

    edge1.getWorld().playSound(getCenter(edge1, edge2), SoundConstants.PAD, 1f, 1f);

    if ((doorType & RE_CLOSING) > 0 && closeAfterTicks > 0) {
      Bukkit.getScheduler()
          .runTaskLater(ArenaShooter.getInstance(), this::close, animationTime + closeAfterTicks);
    }
  }

  private Vector deltaVector(Location source, Location destination) {
    return destination.toVector().subtract(source.toVector());
  }

  public void close() {
    if (!this.open) {
      return;
    }

    edge1.getWorld().playSound(getCenter(edge1, edge2), SoundConstants.PAD, 1f, 1f);

    Location min = getMinimalLocation(edge1, edge2);
    Location max = getMaximalLocation(edge1, edge2);
    Location destCenter = getCenter(min, max);
    Location sourceCenter = destinationCenter.clone();
    Vector delta = deltaVector(sourceCenter, destCenter);

    for (BlockDisplay display : blockDisplays) {
      display.setInterpolationDuration(animationTime);
      display.setInterpolationDelay(-1);
      Transformation transformation = display.getTransformation();
      transformation.getTranslation().set(delta.toVector3d());
      display.setTransformation(transformation);
    }

    Bukkit.getScheduler()
        .runTaskLater(
            ArenaShooter.getInstance(),
            () -> {
              int i = 0;
              for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                  for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block block = min.getWorld().getBlockAt(x, y, z);
                    BlockDisplay display = blockDisplays[i++];
                    block.setBlockData(display.getBlock());
                    display.remove();
                  }
                }
              }
              blockDisplays = null;
              this.open = false;
            },
            animationTime);
  }

  @Override
  public Door clone() {
    try {
      Door door = (Door) super.clone();
      door.edge1 = edge1.clone();
      door.edge2 = edge2.clone();
      door.destinationCenter = destinationCenter.clone();
      return door;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public static final int ONE_TIME = 0;
  public static final int RE_CLOSING = 1;
}
