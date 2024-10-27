package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameFactory;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.worlds.ArenaWorld;

public class TeamDeathMatchGameFactory implements GameFactory {

  @Override
  public Game createGame(ArenaWorld arena) {
//        return new TeamDeathMatchGame(arena, RuleSet.TDM);
    return new TeamDeathMatchGame(arena, RuleSet.DM);
  }
}
