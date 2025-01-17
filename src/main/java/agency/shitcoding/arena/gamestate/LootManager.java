package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.models.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

@Getter
@Setter
public class LootManager {

  private Map<Integer, LootPointInstance> lootPoints;
  private final Game game;

  public LootManager(Collection<LootPoint> template, Game game) {
    this.game = game;
    lootPoints = new HashMap<>(template.size());
    template.stream()
        .sorted(Comparator.comparingInt(LootPoint::getId))
        .filter(lp -> lp.getType() != Powerup.NOTHING)
        .forEach(this::generateInstance);
  }

  public void cleanup() {
    if (lootPoints == null) {
      return;
    }
    for (LootPointInstance lootPoint : lootPoints.values()) {
      Optional.ofNullable(lootPoint.getSpawnTask()).ifPresent(BukkitTask::cancel);
      Location location = lootPoint.getLootPoint().getLocation();
      if (!location.isChunkLoaded()) {
        location.getChunk().load();
      }
      location.getNearbyEntities(3, 3, 3)
          .stream()
          .filter(Item.class::isInstance)
          .forEach(Entity::remove);
    }
  }

  private void generateInstance(LootPoint lootPoint) {
    LootPointInstance instance = new LootPointInstance(lootPoint);
    BukkitTask bukkitTask;

    if (game.getGameRules().fastWeaponSpawn()
        && (lootPoint.getType().getType() == PowerupType.WEAPON || lootPoint.getType().getType() == PowerupType.AMMO)) {
      bukkitTask = Bukkit.getScheduler().runTaskTimer(ArenaShooter.getInstance(), () -> spawnTask(instance),
          0, GameplayConstants.FAST_WEAPON_SPAWN_INTERVAL_TICKS);
    } else {
      bukkitTask =
          Bukkit.getScheduler()
              .runTaskTimer(
                  ArenaShooter.getInstance(),
                  () -> spawnTask(instance),
                  lootPoint.getType().getOffset(),
                  lootPoint.getType().getSpawnInterval());
    }
    instance.setSpawnTask(bukkitTask);

    this.lootPoints.put(instance.getLootPoint().getId(), instance);
  }

  private void spawnTask(LootPointInstance instance) {
    if (!instance.isLooted()) {
      return;
    }
    Powerup powerup = instance.getLootPoint().getType();
    if (powerup == Powerup.QUAD_DAMAGE && game.getMajorBuffTracker().getQuadDamageTicks() != null) {
      return;
    }
    if (powerup == Powerup.PROTECTION && game.getMajorBuffTracker().getProtectionTicks() != null) {
      return;
    }
    ItemStack itemStack = powerup.getItemStack().clone();
    itemStack.lore(List.of(Component.text(UUID.randomUUID().toString())));
    Location location = instance.getLootPoint().getLocation().toCenterLocation().clone();

    location.getNearbyEntities(.5, .5, .5).stream()
        .filter(Item.class::isInstance)
        .map(e -> (Item) e)
        .forEach(Item::remove);

    var item = location.getWorld().dropItem(location,
        itemStack,
        i -> {
          i.getPersistentDataContainer().set(
              Keys.LOOT_POINT_KEY,
              PersistentDataType.INTEGER,
              instance.getLootPoint().getId()
          );
          i.setCanMobPickup(false);
        }
    );
    item.setVelocity(new Vector(0f, .2f, 0f));

    instance.setLooted(false);
  }
}
