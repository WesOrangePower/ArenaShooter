package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class GameOrchestrator {

  @Getter
  private static final GameOrchestrator instance = new GameOrchestrator();
  @Getter
  private final Set<Game> games = new HashSet<>();
  @Getter
  private final Scoreboard scoreboard;

  private GameOrchestrator() {
    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
  }

  public Game createGame(RuleSet ruleSet, Arena arena) {
    Game game = ruleSet.getGameFactory().createGame(arena);
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
  }

  public List<String> getUsedArenaNames() {
    return games.stream().map(Game::getArena).map(Arena::getName).toList();
  }

  public Optional<Game> getGameByArena(String arenaName) {
    return games.stream()
        .filter(game -> game.getArena().getName().equalsIgnoreCase(arenaName))
        .findFirst();
  }
}
