package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.worlds.ArenaWorld;

public class InstagibGameFactory implements GameFactory {

  @Override
  public Game createGame(ArenaWorld arena) {
    return new InstagibGame(arena);
  }
}
