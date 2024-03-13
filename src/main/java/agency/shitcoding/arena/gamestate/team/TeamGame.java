package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.localization.LangContext;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.GameStage;
import agency.shitcoding.arena.models.RuleSet;
import java.util.Set;
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

    var lang = new LangPlayer(player);

    var teamName = lang.getLocalized(team.getTeamMeta().getDisplayName());
    return lang.getLocalized("game.team.youJoined",
        team.getTeamMeta().getTextColor().asHexString(),
        teamName
    );
  }

  @Override
  public void updateScore(Player p, int delta) {
    super.updateScore(p, delta);
  }

  @Override
  protected void updateScoreBoard() {
    for (TeamScore score : teamManager.getScores()) {
      var teamMeta = score.getTeam().getTeamMeta();
      var teamName = new LangContext().getLocalized(teamMeta.getDisplayName());
      var qualifier = teamMeta.getChatColor() + teamName;
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
  public void sendJoinMessage(Player player, Set<Player> players) {
    var team = teamManager.getTeam(player).orElseThrow();

    for (Player aPlayer : players) {
      var lp = LangPlayer.of(aPlayer);
      lp.sendRichLocalized("game.team.playerJoined",
          player.getName(),
          Integer.toString(team.getTeamMeta().getBukkitColor().asRGB(), 16),
          lp.getLocalized(team.getTeamMeta().getDisplayName())
          );
    }
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
      new LangPlayer(player).sendRichLocalized("game.team.full");
    }
    super.addPlayer(player);
  }

  @Override
  public void addPlayer(Player player) {
    throw new IllegalStateException("Use addPlayer(Player player, GameTeam team) instead");
  }

  @Override
  public void endGame(String reason) {
    scoreboard.getTeams().forEach(Team::unregister);
    super.endGame(reason);
  }

  @Override
  protected Component getGameStatComponent() {
    var builder = Component.text();
    teamManager.getScores()
        .forEach(score -> builder.append(score.getTeam().getTeamMeta().getDisplayComponent(new LangContext()))
            .append(Component.text(": " + score.getScore() + "\n")));
    return builder.build();
  }

}
