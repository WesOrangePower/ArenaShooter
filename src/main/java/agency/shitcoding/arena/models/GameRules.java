package agency.shitcoding.arena.models;

import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.Powerup;
import agency.shitcoding.arena.models.Weapon;

import java.util.List;
import java.util.Map;

public interface GameRules {
    Map<Ammo, Integer> spawnAmmo();
    List<Powerup> spawnPowerups();
    int spawnArmor();
    long gameTimerTicks();
    boolean doRespawn();
}
