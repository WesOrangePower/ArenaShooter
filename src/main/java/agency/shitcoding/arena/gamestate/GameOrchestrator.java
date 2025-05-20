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
import org.jetbrains.annotations.Nullable;

public final class GameOrchestrator {

  @Getter private static final GameOrchestrator instance = new GameOrchestrator();
  @Getter private final Set<Game> games = new HashSet<>();
  private final Map<Game, Scoreboard> scoreboards = new HashMap<>();
  /**
   * Used to block players from creating multiple games at once.
   */
  private final Set<Player> lockedPlayers = new HashSet<>();

  private GameOrchestrator() {}

  /**
   * This will block soo hard.
   *
   * @param ruleSet RuleSet
   * @param arena Source arena, template
   * @return the created Game
   */
  public Game createGame(RuleSet ruleSet, Arena arena, GameRules gameRules, @Nullable Player lock)
      throws PlayerLockedException {
    if (lock != null) {
      if (lockedPlayers.contains(lock)) {
        throw new PlayerLockedException();
      }
      lockedPlayers.add(lock);
    }

    try {
      var worldArena = WorldFactory.getInstance().newWorld(arena);

      var game = ruleSet.getGameFactory().createGame(worldArena, gameRules);
      games.add(game);
      return game;
    } finally {
      if (lock != null) {
        lockedPlayers.remove(lock);
      }
    }
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
