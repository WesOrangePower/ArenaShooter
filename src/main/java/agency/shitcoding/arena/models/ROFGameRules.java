package agency.shitcoding.arena.models;


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
  public long gameLengthSeconds() {
    return 6L * 60L;
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
    return 32;
  }

  @Override
  public int minPlayers() {
    return 2;
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
