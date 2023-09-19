package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;

public class LMSGameFactory implements GameFactory {
    @Override
    public Game createGame(Arena arena) {
        return new LMSGame(arena);
    }
}
