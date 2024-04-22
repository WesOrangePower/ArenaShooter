package agency.shitcoding.arena.models;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ROFGameRules implements GameRules {

  @Override
  public Map<Ammo, Integer> spawnAmmo() {
    return Map.of(Ammo.ROCKETS, 6);
  }

  @Override
  public List<Powerup> spawnPowerups() {
    return List.of(Powerup.ROCKET_LAUNCHER);
  }

  @Override
  public int spawnArmor() {
    return 30;
  }

  @Override
  public long gameTimerTicks() {
    return 6L * 60L * 20L;
  }

  @Override
  public boolean doRespawn() {
    return true;
  }

  @Override
  public boolean hasTeams() {
    return false;
  }

  @Override
  public boolean allowJoinAfterStart() {
    return true;
  }

  @Override
  public ItemStack getMenuBaseItem() {
    return new ItemStack(Weapon.ROCKET_LAUNCHER.item);
  }
}
