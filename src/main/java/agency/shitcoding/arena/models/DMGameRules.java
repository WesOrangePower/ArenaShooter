package agency.shitcoding.arena.models;


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
  public long gameLengthSeconds() {
    return 10L * 60L;
  }

  @Override
  public boolean doRespawn() {
    return true;
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