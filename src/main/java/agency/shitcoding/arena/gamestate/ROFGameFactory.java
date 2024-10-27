package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.worlds.ArenaWorld;

public class ROFGameFactory implements GameFactory {

  @Override
  public Game createGame(ArenaWorld arena) {
    return new ROFGame(arena);
  }
}
