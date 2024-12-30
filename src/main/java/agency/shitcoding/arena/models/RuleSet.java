package agency.shitcoding.arena.models;

import agency.shitcoding.arena.gamestate.*;
import agency.shitcoding.arena.gamestate.team.TeamDeathMatchGameFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RuleSet {
  DM("ruleset.dm", 10 * 60, 32, 2, new DeathMatchGameFactory(), new DMGameRules()),
  TDM("ruleset.tdm", 10 * 60, 32, 2, new TeamDeathMatchGameFactory(), new TDMGameRules()),
  LMS("ruleset.lms", 7 * 60, 32, 2, new LMSGameFactory(), new LMSGameRules()),
  INSTAGIB("ruleset.instagib", 8 * 60, 32, 2, new InstagibGameFactory(), new InstagibGameRules()),
  ROF("ruleset.rof", 8 * 60, 8, 2, new ROFGameFactory(), new ROFGameRules()),
  CTF("ruleset.ctf", 10 * 60, 32, 2, new CTFGameFactory(), new CTFGameRules());

  private final String name;
  private final int gameLenSeconds;
  private final int maxPlayers;
  private final int minPlayers;
  private final GameFactory gameFactory;
  private final GameRules gameRules;
}

