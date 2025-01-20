package agency.shitcoding.arena.models;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomGameRules implements GameRules {
  private final Map<Ammo, Integer> spawnAmmo;
  private final List<Powerup> spawnPowerups;
  private final Integer spawnArmor;
  private final Long gameLengthSeconds;
  private final Boolean doRespawn;
  private final Boolean dropMostValuableWeaponOnDeath;
  private final Integer maxPlayers;
  private final Integer minPlayers;
  private final Boolean fastWeaponSpawn;
  private final Boolean showHealth;

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
  public boolean dropMostValuableWeaponOnDeath() {
    return dropMostValuableWeaponOnDeath;
  }

  @Override
  public int maxPlayers() {
    return maxPlayers;
  }

  @Override
  public int minPlayers() {
    return minPlayers;
  }

  @Override
  public boolean fastWeaponSpawn() {
    return fastWeaponSpawn;
  }

  @Override
  public boolean showHealth() {
    return showHealth;
  }
}
