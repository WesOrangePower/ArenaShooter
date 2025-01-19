package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.models.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;

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
      location.getNearbyEntities(3, 3, 3).stream()
          .filter(Item.class::isInstance)
          .forEach(Entity::remove);
    }
  }

  private void generateInstance(LootPoint lootPoint) {
    LootPointInstance instance = new LootPointInstance(lootPoint);
    instance.setLooted(true);
    BukkitTask bukkitTask;

    var spawnTask = new LootSpawnTask(game, instance);

    if (game.getGameRules().fastWeaponSpawn()
        && (lootPoint.getType().getType() == PowerupType.WEAPON
            || lootPoint.getType().getType() == PowerupType.AMMO)) {
      bukkitTask =
          Bukkit.getScheduler()
              .runTaskTimer(
                  ArenaShooter.getInstance(),
                  spawnTask,
                  0,
                  GameplayConstants.FAST_WEAPON_SPAWN_INTERVAL_TICKS);
    } else {
      bukkitTask =
          Bukkit.getScheduler()
              .runTaskTimer(
                  ArenaShooter.getInstance(),
                  spawnTask,
                  lootPoint.getType().getOffset(),
                  lootPoint.getType().getSpawnInterval());
    }
    instance.setSpawnTask(bukkitTask);

    this.lootPoints.put(instance.getLootPoint().getId(), instance);
  }
}
