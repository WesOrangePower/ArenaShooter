package agency.shitcoding.arena.models;


import java.util.List;
import java.util.Map;

public class InstagibGameRules implements GameRules {

  @Override
  public Map<Ammo, Integer> spawnAmmo() {
    return Map.of(Ammo.CELLS, 50);
  }

  @Override
  public List<Powerup> spawnPowerups() {
    return List.of(Powerup.RAILGUN);
  }

  @Override
  public int spawnArmor() {
    return 0;
  }

  @Override
  public long gameLengthSeconds() {
    return 5L * 60L;
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
}
