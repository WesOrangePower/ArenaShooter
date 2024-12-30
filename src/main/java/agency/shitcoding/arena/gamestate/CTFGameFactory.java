package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.worlds.ArenaWorld;

public class CTFGameFactory implements GameFactory {

  @Override
  public Game createGame(ArenaWorld arenaWorld) {
    return new CTFGame(arenaWorld);
  }
}
