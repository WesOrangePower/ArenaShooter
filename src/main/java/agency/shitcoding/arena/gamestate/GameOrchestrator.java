package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GameOrchestrator {
    @Getter
    private final Set<Game> games = new HashSet<>();
    @Getter
    private final Scoreboard scoreboard;

    private GameOrchestrator() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }
    @Getter
    private final static GameOrchestrator instance = new GameOrchestrator();

    public Game createGame(RuleSet ruleSet, Arena arena) {
        Game game = ruleSet.getGameFactory().createGame(arena);
        games.add(game);
        game.startAwaiting();
        return game;
    }

    public Optional<Game> getGameByPlayer(Player player) {
        return games.stream().filter(game -> game.getPlayers().contains(player)).findFirst();
    }

    public void removeGame(Game game) {
        games.remove(game);
    }
}
