package agency.shitcoding.arena.models;


import java.util.List;
import java.util.Map;

public class TutorialGameRules implements GameRules {
  @Override
  public Map<Ammo, Integer> spawnAmmo() {
    return Map.of();
  }

  @Override
  public List<Powerup> spawnPowerups() {
    return List.of();
  }

  @Override
  public int spawnArmor() {
    return 0;
  }

  @Override
  public long gameLengthSeconds() {
    return 3600;
  }

  @Override
  public boolean doRespawn() {
    return true;
  }

  @Override
  public boolean dropMostValuableWeaponOnDeath() {
    return false;
  }

  @Override
  public int maxPlayers() {
    return 1;
  }

  @Override
  public int minPlayers() {
    return 1;
  }

  @Override
  public boolean fastWeaponSpawn() {
    return false;
  }

  @Override
  public boolean showHealth() {
    return false;
  }
}
