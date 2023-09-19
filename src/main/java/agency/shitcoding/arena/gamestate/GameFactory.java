package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;

public abstract class AbstractGameFactory {
    public abstract Game createGame(Arena arena);
}
