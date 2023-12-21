package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;

public interface GameFactory {
    Game createGame(Arena arena);
}
