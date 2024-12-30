package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.models.LootPoint;
import agency.shitcoding.arena.models.LootPointFilter;
import agency.shitcoding.arena.models.LootPointMarker;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.worlds.ArenaWorld;
import org.bukkit.entity.Player;

public class CTFGame extends TeamGame {
  FlagManager flagManager;
  protected LootPointFilter lootPointFilter = this::lootPointFilter;


  @Override
  public LootPointFilter getLootPointFilter() {
    return lootPointFilter;
  }

  public CTFGame(ArenaWorld arenaWorld) {
    super(arenaWorld, RuleSet.CTF);

    var lps = arenaWorld.getShifted().getLootPoints();
    var hasRedTeamBase = lps.stream().anyMatch(lp -> (lp.getMarkers() & LootPointMarker.RED_TEAM_BASE.getValue()) != 0);
    var hasBlueTeamBase = lps.stream().anyMatch(lp -> (lp.getMarkers() & LootPointMarker.BLUE_TEAM_BASE.getValue()) != 0);
    var hasRedTeamSpawn = lps.stream().anyMatch(lp -> (lp.getMarkers() & LootPointMarker.RED_TEAM_SPAWN.getValue()) != 0);
    var hasBlueTeamSpawn = lps.stream().anyMatch(lp -> (lp.getMarkers() & LootPointMarker.BLUE_TEAM_SPAWN.getValue()) != 0);
    if (!hasRedTeamBase || !hasBlueTeamBase) {
      throw new IllegalStateException("CTF game must have both red and blue team bases");
    }
    if (!hasRedTeamSpawn || !hasBlueTeamSpawn) {
      throw new IllegalStateException("CTF game must have both red and blue team spawns");
    }
  }

  @Override
  public void updateScore(Player p, int delta) {
    /*
     Score does not update on kills
     */
  }

  private boolean lootPointFilter(LootPoint lootPoint, Player player) {
    var team = getTeamManager().getTeam(player).orElseThrow(() -> new IllegalStateException("Player" + player.getName() + " not in team"));

    switch (team.getETeam()) {
      case BLUE -> {
        return (lootPoint.getMarkers() & LootPointMarker.BLUE_TEAM_SPAWN.getValue()) > 0;
      }
      case RED -> {
        return (lootPoint.getMarkers() & LootPointMarker.RED_TEAM_SPAWN.getValue()) > 0;
      }
    }
    return false;
  }
}
