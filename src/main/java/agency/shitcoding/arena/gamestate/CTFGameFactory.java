package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.GameRules;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.worlds.ArenaWorld;

public class CTFGameFactory implements GameFactory {

  @Override
  public Game createGame(ArenaWorld arenaWorld, GameRules gameRules) {
    return new CTFGame(arenaWorld, RuleSet.CTF, gameRules);
  }
}
