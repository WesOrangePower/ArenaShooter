package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.GameRules;
import agency.shitcoding.arena.worlds.ArenaWorld;

public interface GameFactory {

  Game createGame(ArenaWorld arenaWorld, GameRules gameRules);
}
