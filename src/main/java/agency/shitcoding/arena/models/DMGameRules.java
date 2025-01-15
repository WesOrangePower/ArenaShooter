package agency.shitcoding.arena.models;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class DMGameRules implements GameRules {

  @Override
  public Map<Ammo, Integer> spawnAmmo() {
    return Map.of(Ammo.BULLETS, 50);
  }

  @Override
  public List<Powerup> spawnPowerups() {
    return List.of(
        Powerup.MACHINE_GUN,
        Powerup.GAUNTLET
    );
  }

  @Override
  public int spawnArmor() {
    return 0;
  }

  @Override
  public long gameTimerTicks() {
    return 5L * 60L * 20L;
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
    return new ItemStack(Weapon.GAUNTLET.item);
  }

  @Override
  public boolean dropMostValuableWeaponOnDeath() {
    return true;
  }
}