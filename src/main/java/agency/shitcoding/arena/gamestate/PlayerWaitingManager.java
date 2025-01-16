package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.localization.LangPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
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
    if (game.getPlayers().size() >= game.getGameRules().minPlayers()) {
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

    game.getPlayers().stream()
        .map(LangPlayer::new)
        .forEach(p -> p.sendRichLocalized(
            "game.waiting.waitingForPlayers",
                game.getPlayers().size(),
                game.getGameRules().minPlayers()
            )
        );
  }

  int secondsElapsed;

  private void startGameCycle() {
    int untilStart = SECONDS_TO_START - secondsElapsed++;
    if (untilStart % 5 == 0 || untilStart <= 5) {
      game.getPlayers().stream().map(LangPlayer::new)
          .forEach(p -> p.sendRichLocalized(
              "game.waiting.startTimer",
              game.getPlayers().size(),
              game.getGameRules().maxPlayers(),
              untilStart
          ));
    }

    if (secondsElapsed >= SECONDS_TO_START) {
      game.startGame();
    }

  }

  public void cleanup() {
    if (gameStartingTask != null) {
      gameStartingTask.cancel();
    }
    if (playerWaitingTimerTask != null) {
      playerWaitingTimerTask.cancel();
    }
  }
}
