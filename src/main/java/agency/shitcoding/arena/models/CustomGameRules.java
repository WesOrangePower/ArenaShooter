package agency.shitcoding.arena.models;

import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomGameRules implements GameRules {
  private final Map<Ammo, Integer> spawnAmmo ;
  private final List<Powerup> spawnPowerups ;
  private final Integer spawnArmor ;
  private final Long gameLengthSeconds ;
  private final Boolean doRespawn ;
  private final Boolean hasTeams ;
  private final ItemStack getMenuBaseItem ;
  private final Boolean dropMostValuableWeaponOnDeath ;

  @Override
  public Map<Ammo, Integer> spawnAmmo() {
    return spawnAmmo;
  }

  @Override
  public List<Powerup> spawnPowerups() {
    return spawnPowerups;
  }

  @Override
  public int spawnArmor() {
    return spawnArmor;
  }

  @Override
  public long gameLengthSeconds() {
    return gameLengthSeconds;
  }

  @Override
  public boolean doRespawn() {
    return doRespawn;
  }

  @Override
  public boolean hasTeams() {
    return hasTeams;
  }

  @Override
  public ItemStack getMenuBaseItem() {
    return getMenuBaseItem;
  }

  @Override
  public boolean dropMostValuableWeaponOnDeath() {
    return dropMostValuableWeaponOnDeath;
  }
}
