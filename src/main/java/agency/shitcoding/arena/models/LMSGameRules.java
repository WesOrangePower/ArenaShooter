package agency.shitcoding.arena.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class LMSGameRules implements GameRules {

  @Override
  public Map<Ammo, Integer> spawnAmmo() {
    return Map.of(Ammo.BULLETS, 50);
  }

  @Override
  public List<Powerup> spawnPowerups() {
    return List.of(Powerup.PROTECTION, Powerup.GAUNTLET, Powerup.MACHINE_GUN);
  }

  @Override
  public int spawnArmor() {
    return 0;
  }

  @Override
  public long gameTimerTicks() {
    return 8L * 60L * 20L;
  }

  @Override
  public boolean doRespawn() {
    return false;
  }

  @Override
  public boolean hasTeams() {
    return false;
  }

  @Override
  public boolean allowJoinAfterStart() {
    return false;
  }

  @Override
  public ItemStack getMenuBaseItem() {
    return new ItemStack(Material.GOLDEN_APPLE);
  }
}
