package agency.shitcoding.arena.models;

import java.util.List;
import java.util.Map;

public interface GameRules {
    Map<Ammo, Integer> spawnAmmo();

    List<Powerup> spawnPowerups();

    int spawnArmor();

    long gameTimerTicks();

    boolean doRespawn();

    boolean hasTeams();

    boolean allowPlayAfterStart();
}
