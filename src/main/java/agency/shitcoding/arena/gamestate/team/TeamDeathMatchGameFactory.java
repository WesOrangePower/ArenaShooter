package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameFactory;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;

public class TeamDeathMatchGameFactory implements GameFactory {
    @Override
    public Game createGame(Arena arena) {
//        return new TeamDeathMatchGame(arena, RuleSet.TDM);
        return new TeamDeathMatchGame(arena, RuleSet.DM);
    }
}
