package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;

public class InstagibGameFactory implements GameFactory {

  @Override
  public Game createGame(Arena arena) {
    return new InstagibGame(arena);
  }
}
