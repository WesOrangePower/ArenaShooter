package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.GameStage;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.models.exceptions.TeamFullException;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

@Getter
public abstract class TeamGame extends Game {

  protected TeamManager teamManager;

  protected TeamGame(Arena arena, RuleSet ruleSet) {
    super(arena, ruleSet);
    int maxPerTeam = ruleSet.getMaxPlayers() / 2;
    teamManager = new TeamManager(maxPerTeam, getScoreboard());
  }

  @Override
  public String youJoinedGameMessage(Player player) {
    GameTeam team = teamManager.getTeam(player).orElseThrow();
    return "<green> + вы присоединились к команде <color:"
        + team.getTeamMeta().getTextColor().asHexString() + ">"
        + team.getTeamMeta().getDisplayName();
  }

  @Override
  public void updateScore(Player p, int delta) {
    super.updateScore(p, delta);
  }

  @Override
  protected void updateScoreBoard() {
    for (TeamScore score : teamManager.getScores()) {
      var teamMeta = score.getTeam().getTeamMeta();
      var qualifier = teamMeta.getChatColor() + teamMeta.getDisplayName();
      scoreboardObjective.getScore(qualifier).setScore(score.getScore());
    }
  }

  @Override
  public void doUpdateScore(Player p, int delta) {
    var optTeam = teamManager.getTeam(p);
    if (optTeam.isEmpty()) {
      return;
    }
    var team = optTeam.get();
    var score = teamManager.getScore(team);
    teamManager.setScore(team, Math.max(score + delta, 0));
  }

  @Override
  public String joinBroadcastMessage(Player player) {
    var team = teamManager.getTeam(player).orElseThrow();
    return String.format("<green>%s присоединился к команде <color:#%s>%s",
        player.getName(),
        Integer.toString(team.getTeamMeta().getBukkitColor().asRGB(), 16),
        team.getTeamMeta().getDisplayName());
  }

  @Override
  public String leaveBroadcastMessage(Player player) {
    return super.leaveBroadcastMessage(player);
  }

  @Override
  public void removePlayer(Player player) {
    super.removePlayer(player);
    teamManager.removeFromTeam(player);
    if (gamestage == GameStage.IN_PROGRESS) {
      recalculateScore();
    }
  }

  public void addPlayer(Player player, ETeam team) {
    boolean added = teamManager.addToTeam(player, team);
    if (!added) {
      player.sendMessage("<dark_red>Команда переполнена!");
    }
    super.addPlayer(player);
  }

  @Override
  public void addPlayer(Player player) {
    throw new IllegalStateException("Use addPlayer(Player player, GameTeam team) instead");
  }

  public void assignTeam(Player p, ETeam team) throws TeamFullException {
    if (!teamManager.addToTeam(p, team)) {
      throw new TeamFullException();
    }
  }

  @Override
  public void endGame(String reason) {
    scoreboard.getTeams().forEach(Team::unregister);
    super.endGame(reason);
  }

  @Override
  protected Component getGameStatComponent() {
    var builder = Component.text();
    teamManager.getScores().forEach(score -> builder.append(score.getTeam().getTeamMeta().getDisplayComponent())
        .append(Component.text(": " + score.getScore() + "\n")));
    return builder.build();
  }

}
