package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.GameRules;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.worlds.WorldFactory;

import java.util.*;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class GameOrchestrator {

  @Getter private static final GameOrchestrator instance = new GameOrchestrator();
  @Getter private final Set<Game> games = new HashSet<>();
  private final Map<Game, Scoreboard> scoreboards = new HashMap<>();

  private GameOrchestrator() {
  }

  /**
   * This will block soo hard.
   *
   * @param ruleSet RuleSet
   * @param arena Source arena, template
   * @return the created Game
   */
  public Game createGame(RuleSet ruleSet, Arena arena, GameRules gameRules) {

    var worldArena = WorldFactory.getInstance().newWorld(arena);

    Game game = ruleSet.getGameFactory().createGame(worldArena, gameRules);
    games.add(game);
    return game;
  }

  public Optional<Game> getGameByPlayer(Player player) {
    return games.stream().filter(game -> game.getPlayers().contains(player)).findFirst();
  }

  public List<Arena> getUsedArenas() {
    return games.stream().map(Game::getArena).toList();
  }

  public void removeGame(Game game) {
    games.remove(game);
    scoreboards.remove(game);
    game.getArenaWorld().destroy();
  }

  public List<String> getUsedArenaNames() {
    return games.stream().map(Game::getArena).map(Arena::getName).toList();
  }

  public Optional<Game> getGameByArena(String arenaName) {
    return games.stream()
        .filter(game -> game.getArena().getName().equalsIgnoreCase(arenaName))
        .findFirst();
  }

  public Optional<Game> getGameByHashCode(Integer gameHash) {
    return games.stream().filter(game -> game.hashCode() == gameHash).findFirst();
  }

  public void unregisterScoreboard() {
    for (Scoreboard scoreboard : scoreboards.values()) {
      scoreboard.getObjectives().forEach(Objective::unregister);
      scoreboard.getTeams().forEach(Team::unregister);
    }
  }

  public Scoreboard getScoreboard(Game game) {
    return scoreboards.computeIfAbsent(game, g -> Bukkit.getScoreboardManager().getNewScoreboard());
  }
}
