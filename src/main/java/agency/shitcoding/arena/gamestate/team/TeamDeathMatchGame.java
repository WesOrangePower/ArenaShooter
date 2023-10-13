package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.LootPoint;
import agency.shitcoding.arena.models.RuleSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TeamDeathMatchGame extends TeamGame {
    protected TeamDeathMatchGame(Arena arena, RuleSet ruleSet) {
        super(arena, ruleSet);
    }

    @Override
    protected @NotNull Set<LootPoint> preprocessLootPoints(Set<LootPoint> lootPoints) {
        return lootPoints;
    }

    @Override
    protected void startGameStage2() {

    }
}
