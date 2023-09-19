package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;

public class LMSGame extends Game {
    public LMSGame(Arena arena) {
        super(arena, RuleSet.LMS);
    }

    @Override
    protected void startGameStage2() {

    }
}
