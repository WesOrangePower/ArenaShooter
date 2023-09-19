package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;

public class DeathMatchGame extends Game {
    public DeathMatchGame(Arena arena) {
        super(arena, RuleSet.DM);
    }

    @Override
    protected void startGameStage2() {

    }
}
