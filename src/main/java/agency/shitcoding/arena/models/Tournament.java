package agency.shitcoding.arena.models;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.PlayerScore;
import agency.shitcoding.arena.gamestate.TournamentAccessor;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.gamestate.team.TeamScore;
import agency.shitcoding.arena.localization.LangPlayer;
import com.google.common.base.Preconditions;
import io.vavr.control.Either;

import java.util.*;
import java.util.function.Consumer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

@Getter
public class Tournament {
  private final RuleSet ruleSet;
  private final int gameCount;
  private final int maxPlayerCount;
  private final Arena[] arenas;
  private final List<String> playerNames = new ArrayList<>();
  private final Map<String, ETeam> playerTeams;
  private final Game[] games;
  private final boolean publicJoin;
  private int arenaPointer = 0;
  private Game currentGame;

  public Tournament(
      boolean publicJoin, RuleSet ruleSet, int gameCount, int maxPlayerCount, Arena[] arenas) {
    Preconditions.checkNotNull(ruleSet);
    Preconditions.checkNotNull(arenas);
    Preconditions.checkArgument(gameCount > 0);
    Preconditions.checkArgument(maxPlayerCount > 1);
    Preconditions.checkArgument(arenas.length > 0);

    this.publicJoin = publicJoin;
    this.ruleSet = ruleSet;
    this.gameCount = gameCount;
    this.maxPlayerCount = maxPlayerCount;
    this.arenas = arenas;
    this.games = new Game[gameCount];
    if (ruleSet.getGameRules().hasTeams()) {
      playerTeams = new HashMap<>();
    } else {
      playerTeams = null;
    }
  }

  public boolean removePlayer(String player) {
    if (isTeamTournament()) playerTeams.remove(player);
    return playerNames.remove(player);
  }

  public Arena nextArena() {
    return arenas[arenaPointer++ % arenas.length];
  }

  public Arena peekNextArena() {
    return arenas[arenaPointer % arenas.length];
  }

  public boolean isTeamTournament() {
    return playerTeams != null;
  }

  public int getCurrentGameNumber() {
    return arenaPointer;
  }

  public Either<String, Void> addPlayer(Player player, ETeam team) {
    if (playerNames.size() >= maxPlayerCount) {
      return Either.left("Tournament is full");
    }
    if (playerNames.contains(player.getName())) {
      return Either.left("Player already in tournament");
    }
    if (ruleSet.getGameRules().hasTeams() && team == null) {
      return Either.left("Team required");
    }

    LangPlayer langPlayer = LangPlayer.of(player);
    if (isTeamTournament()) {
      if (playerTeams.containsKey(player.getName())) {
        return Either.left("Player already in tournament");
      }
      playerTeams.put(player.getName(), team);
      playerNames.add(player.getName());
      langPlayer.sendRichLocalized(
          "tournament.add.team.success",
          miniMessage()
              .serialize(team.getTeamMeta().getDisplayComponent(langPlayer.getLangContext())));
      return Either.right(null);
    }

    playerNames.add(player.getName());
    langPlayer.sendRichLocalized("tournament.add.success");

    return Either.right(null);
  }

  public void hostNextGame() {
    if (currentGame != null) {
      GameOrchestrator.getInstance().removeGame(currentGame);
    }

    if (arenaPointer >= gameCount) {
      return;
    }

    final int gamePointer = arenaPointer;
    currentGame = GameOrchestrator.getInstance().createGame(ruleSet, nextArena(), null);
    games[gamePointer] = currentGame;

    for (String playerName : playerNames) {
      var p = Bukkit.getPlayer(playerName);
      if (p != null) {
        if (playerTeams != null) {
          ((TeamGame) currentGame).addPlayer(p, playerTeams.get(playerName));
        } else {
          currentGame.addPlayer(p);
        }
      }
    }
  }

  public void endTournament() {
    if (currentGame != null) {
      currentGame.endGame("game.end.tournamentEnd", false);
    }

    broadcastToPlayers(lp -> lp.sendRichLocalized("tournament.end"));

    broadcastToPlayers(lp -> lp.sendRichLocalized("tournament.score.header"));
    if (isTeamTournament()) {
      List<TeamScore> teamScores = sumTeamScores();
      for (TeamScore teamScore : teamScores) {
        broadcastToPlayers(
            lp ->
                lp.sendRichLocalized(
                    "tournament.score.team",
                    lp.getLocalized(teamScore.getTeam().getTeamMeta().getDisplayName()),
                    teamScore.getScore()));
      }
    } else {
      List<PlayerScore> playerScores = sumScores();
      for (PlayerScore playerScore : playerScores) {
        broadcastToPlayers(
            lp ->
                lp.sendRichLocalized(
                    "tournament.score.player",
                    playerScore.getPlayer().getName(),
                    playerScore.getScore()));
      }
    }

    TournamentAccessor.getInstance().setTournament(null);
  }

  private void broadcastToPlayers(Consumer<LangPlayer> lpc) {
    for (String playerName : playerNames) {
      var p = Bukkit.getPlayer(playerName);
      if (p != null) {
        lpc.accept(LangPlayer.of(p));
      }
    }
  }

  private List<TeamScore> sumTeamScores() {
    Map<ETeam, Integer> scores = new EnumMap<>(ETeam.class);

    List<TeamScore> raw = new ArrayList<>();
    for (Game game : games) {
      if (game != null) {
        Queue<TeamScore> gameScores = ((TeamGame) game).getTeamManager().getScores();

        for (TeamScore score : gameScores) {
          ETeam eTeam = score.getTeam().getETeam();
          if (scores.containsKey(eTeam)) {
            scores.put(eTeam, scores.get(eTeam) + score.getScore());
          } else {
            scores.put(eTeam, score.getScore());
          }
        }
      }
    }
    return raw;
  }

  private List<PlayerScore> sumScores() {
    List<PlayerScore> raw = new ArrayList<>();
    for (Game game : games) {
      if (game != null) {
        raw.addAll(game.getScores());
      }
    }
    return sumScores(raw);
  }

  private List<PlayerScore> sumScores(List<PlayerScore> raw) {
    Map<String, PlayerScore> scores = new HashMap<>();
    for (PlayerScore score : raw) {
      if (scores.containsKey(score.getPlayer().getName())) {
        PlayerScore playerScore = scores.get(score.getPlayer().getName());
        playerScore.setScore(playerScore.getScore() + score.getScore());
      } else {
        scores.put(score.getPlayer().getName(), score);
      }
    }
    return new ArrayList<>(scores.values());
  }

  public void endGame() {
    currentGame = null;
  }

  public ETeam nextAutoAssignedTeam() {
    if (!isTeamTournament()) return null;
    return Arrays.stream(ETeam.values())
        .min(
            Comparator.comparingInt(
                team -> (int) playerTeams.values().stream().filter(team::equals).count()))
        .orElseGet(() -> ETeam.values()[new Random().nextInt(ETeam.values().length)]);
  }
}
