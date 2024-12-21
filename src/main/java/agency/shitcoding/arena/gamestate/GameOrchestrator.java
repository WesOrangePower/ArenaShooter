package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.worlds.WorldFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

public final class GameOrchestrator {

  @Getter private static final GameOrchestrator instance = new GameOrchestrator();
  @Getter private final Set<Game> games = new HashSet<>();
  @Getter private final Scoreboard scoreboard;

  private GameOrchestrator() {
    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
  }

  /**
   * This will block soo hard.
   *
   * @param ruleSet RuleSet
   * @param arena Source arena, template
   * @param hoster Nullable, sends a start message if exists
   * @return the created Game
   */
  public Game createGame(RuleSet ruleSet, Arena arena, @Nullable Player hoster) {
    LangPlayer langPlayer = LangPlayer.of(hoster);

    var worldArena = WorldFactory.getInstance().newWorld(arena);

    Game game = ruleSet.getGameFactory().createGame(worldArena);
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
}
