package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.ArenaShooter;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
public class TeamManager {

  private final int maxPerTeam;
  private final Map<ETeam, GameTeam> teams;
  private final Queue<TeamScore> scores = new PriorityQueue<>(
      (o1, o2) -> o2.getScore() - o1.getScore()
  );

  public TeamManager(int maxPerTeam, Scoreboard scoreboard) {
    this.maxPerTeam = maxPerTeam;
    teams = new EnumMap<>(ETeam.class);
    try {
      for (ETeam value : ETeam.values()) {
        var nac = value.getTeamClass().getConstructor(Team.class);
        var scoreBoardTeam = scoreboard.registerNewTeam(value.name());
        GameTeam team = nac.newInstance(scoreBoardTeam);
        teams.put(value, team);
        scores.add(new TeamScore(team));
      }
    } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
             IllegalAccessException e) {
      ArenaShooter.getInstance().getLogger()
          .severe("Failed to instantiate TeamManager, " +
              "check if all teams have a no-args constructor");
      throw new RuntimeException("Failed to instantiate teams", e);
    }
  }

  /**
   * Adds player to team
   *
   * @return false if full, true if successful
   */
  public boolean addToTeam(Player player, ETeam gameTeam) {
    Set<Player> teamPlayers = teams.get(gameTeam).getPlayers();
    if (teamPlayers.size() >= maxPerTeam && !player.hasPermission("arena.fullTeamJoin")) {
      return false;
    }
    teamPlayers.add(player);
    return true;
  }

  public int getScore(GameTeam team) {
    return scores.stream()
        .filter(e -> e.getTeam().equals(team))
        .findFirst()
        .map(TeamScore::getScore)
        .orElse(0);
  }

  public GameTeam getTeam(ETeam team) {
    return teams.get(team);
  }

  public Optional<GameTeam> getTeam(Player player) {
    for (GameTeam team : teams.values()) {
      if (team.getPlayers().contains(player)) {
        return Optional.of(team);
      }
    }
    return Optional.empty();
  }

  public void setScore(GameTeam team, int i) {
    for (TeamScore score : scores) {
      if (score.getTeam() == team) {
        score.setScore(i);
        return;
      }
    }
    throw new IllegalStateException("Tried to add score to non-existing team");
  }

  public void removeFromTeam(Player player) {
    getTeam(player).ifPresent(gameTeam -> gameTeam.getPlayers().remove(player));
  }
}
