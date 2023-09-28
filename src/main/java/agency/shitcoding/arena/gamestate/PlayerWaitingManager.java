package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public class PlayerWaitingManager {

    private final Game game;
    private BukkitTask playerWaitingTimerTask;
    private BukkitTask gameStartingTask;
    public static final int SECONDS_TO_START = 30;


    public void startAwaiting() {
        this.playerWaitingTimerTask = Bukkit.getScheduler().runTaskTimer(
                ArenaShooter.getInstance(),
                this::awaitingCycle,
                20,
                20L * 5
        );
    }

    private void awaitingCycle() {
        if (game.getPlayers().size() >= game.getRuleSet().getMinPlayers()) {
            if (gameStartingTask == null) {
                secondsElapsed = 0;
                gameStartingTask = Bukkit.getScheduler().runTaskTimer(
                        ArenaShooter.getInstance(),
                        this::startGameCycle,
                        0,
                        20L
                );
            }
            return;
        }

        if (gameStartingTask != null) {
            gameStartingTask.cancel();
            secondsElapsed = 0;
            gameStartingTask = null;
            return;
        }

        game.getPlayers().forEach(p -> p.sendRichMessage(
                        String.format("<yellow>[%s/%s] <green>Ожидание игроков...",
                                game.getPlayers().size(),
                                game.getRuleSet().getMinPlayers()
                        )
                )
        );
    }
    int secondsElapsed;
    private void startGameCycle() {
        int untilStart =  SECONDS_TO_START - secondsElapsed++;
        if (untilStart % 5 == 0 || untilStart <= 5) {
            for (Player player : game.getPlayers()) {
                player.sendRichMessage(
                        String.format("<yellow>[%s/%s] <dark_red>Стартуем через: %ds...",
                                game.getPlayers().size(),
                                game.getRuleSet().getMaxPlayers(),
                                untilStart
                        ));
            }
        }

        if (secondsElapsed >= SECONDS_TO_START) {
            game.startGame();
        }

    }

    public void cleanup() {
        if (gameStartingTask != null)
            gameStartingTask.cancel();
        if (playerWaitingTimerTask != null)
            playerWaitingTimerTask.cancel();
    }
}
