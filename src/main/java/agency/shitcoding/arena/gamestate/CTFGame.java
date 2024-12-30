package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.models.LootPointFilter;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.worlds.ArenaWorld;
import org.bukkit.entity.Player;

import java.util.EnumMap;

public class CTFGame extends TeamGame {
  EnumMap<ETeam, LootPointFilter> flagFilters = new EnumMap<>(ETeam.class);
  FlagManager flagManager;
  public CTFGame(ArenaWorld arenaWorld) {
    super(arenaWorld, RuleSet.CTF);
//    flagFilters.put(ETeam.RED, lp -> lp.getTags().contains("redSpawn"));
//    flagFilters.put(ETeam.BLUE, lp -> lp.getTags().contains("blueSpawn"));
  }

  @Override
  public void updateScore(Player p, int delta) {
    /*
     Score does not update on kills
     */
  }
}
