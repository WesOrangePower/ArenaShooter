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
    public long gameTimerTicks() {
        return 5L * 60L * 20L;
    }

    @Override
    public boolean doRespawn() {
        return true;
    }
}
