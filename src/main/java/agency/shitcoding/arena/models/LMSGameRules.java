package agency.shitcoding.arena.models;


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
  public long gameLengthSeconds() {
    return 8L * 60L;
  }

  @Override
  public boolean doRespawn() {
    return false;
  }

  @Override
  public boolean dropMostValuableWeaponOnDeath() {
    return true;
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
