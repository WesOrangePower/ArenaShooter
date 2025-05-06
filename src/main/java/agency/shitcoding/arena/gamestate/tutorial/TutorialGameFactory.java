package agency.shitcoding.arena.gamestate.tutorial;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameFactory;
import agency.shitcoding.arena.models.GameRules;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.worlds.ArenaWorld;

public class TutorialGameFactory implements GameFactory {

  @Override
  public Game createGame(ArenaWorld arenaWorld, GameRules gameRules) {
    return new TutorialGame(arenaWorld, RuleSet.TUTORIAL, gameRules);
  }
}
