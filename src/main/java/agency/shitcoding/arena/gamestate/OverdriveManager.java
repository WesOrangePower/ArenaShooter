package agency.shitcoding.arena.gamestate;

import static agency.shitcoding.arena.GameplayConstants.BASE_HEALTH;
import static agency.shitcoding.arena.GameplayConstants.MAX_ARMOR;
import static agency.shitcoding.arena.GameplayConstants.OVERDRIVE_ARMOR_TICK;
import static agency.shitcoding.arena.GameplayConstants.OVERDRIVE_HEALTH_TICK;

import agency.shitcoding.arena.ArenaShooter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class OverdriveManager {
  private static final Set<Player> overdriveSet = new HashSet<>();
  private static BukkitTask task = null;
  private static final AtomicInteger overdriveArmorTickCounter = new AtomicInteger(10);

  public static void setHealth(Player player, double health) {
    var attribute = player.getAttribute(Attribute.MAX_HEALTH);
    if (attribute == null) {
      return;
    }
    attribute.setBaseValue(health);
    player.setHealth(health);
    overdriveSet.add(player);
    runTask();
  }

  public static void setArmor(Player player, int armor) {
    if (player.getLevel() > armor) {
      return;
    }
    player.setLevel(armor);
    overdriveSet.add(player);
    runTask();
  }

  private static void runTask() {
    if (task == null) {
      task = Bukkit.getScheduler().runTaskTimer(ArenaShooter.getInstance(),
          OverdriveManager::timerTask,
          0,
          1);
    }
  }

  private static void timerTask() {
    for (Player player : overdriveSet) {
      var attribute = player.getAttribute(Attribute.MAX_HEALTH);
      if (attribute == null) {
        continue;
      }

      if (player.getLevel() <= MAX_ARMOR && player.getHealth() <= BASE_HEALTH) {
        attribute.setBaseValue(BASE_HEALTH);
        overdriveSet.remove(player);
      }

      if (OVERDRIVE_HEALTH_TICK && player.getHealth() > BASE_HEALTH) {
        double newValue = attribute.getBaseValue() - .02;
        if (Math.abs(player.getHealth() - attribute.getBaseValue()) < .003) {
          player.setHealth(newValue);
        }
        attribute.setBaseValue(newValue);
      }

      if (OVERDRIVE_ARMOR_TICK && player.getLevel() > MAX_ARMOR) {
        if (overdriveArmorTickCounter.getAndDecrement() == 0) {
          player.setLevel(player.getLevel() - 1);
          overdriveArmorTickCounter.set(10);
        }
      }
    }
  }

  private OverdriveManager() {}

}
