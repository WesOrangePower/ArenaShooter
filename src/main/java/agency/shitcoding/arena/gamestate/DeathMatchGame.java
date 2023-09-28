package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.LootPoint;
import agency.shitcoding.arena.models.RuleSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DeathMatchGame extends Game {
    public DeathMatchGame(Arena arena) {
        super(arena, RuleSet.DM);
    }

    @Override
    protected @NotNull Set<LootPoint> preprocessLootPoints(Set<LootPoint> lootPoints) {
        return lootPoints;
    }

    @Override
    protected void startGameStage2() {

    }
}
