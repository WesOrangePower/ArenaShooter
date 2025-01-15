package agency.shitcoding.arena.models;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface GameRules {

  Map<Ammo, Integer> spawnAmmo();

  List<Powerup> spawnPowerups();

  int spawnArmor();

  long gameTimerTicks();

  boolean doRespawn();

  boolean hasTeams();

  @Deprecated
  boolean allowJoinAfterStart();

  ItemStack getMenuBaseItem();

  boolean dropMostValuableWeaponOnDeath();
}
