package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.worlds.ArenaWorld;

public class LMSGameFactory implements GameFactory {

  @Override
  public Game createGame(ArenaWorld arena) {
    return new LMSGame(arena);
  }
}
