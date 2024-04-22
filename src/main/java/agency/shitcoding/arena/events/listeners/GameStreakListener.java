package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameStreakUpdateEvent;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.PlayerScore;
import agency.shitcoding.arena.models.PlayerStreak;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class GameStreakListener implements Listener {

  @EventHandler
  public void onGameDamage(GameDamageEvent event) {
    Player player = event.getDealer();
    if (player == null) {
      return;
    }

    Optional<Game> optionalGame = GameOrchestrator.getInstance().getGameByPlayer(player);

    if (optionalGame.isEmpty()) {
      return;
    }

    Game game = optionalGame.get();
    PlayerScore score = game.getScore(player);
    if (score == null) {
      return;
    }

    Weapon weapon = event.getWeapon();
    PlayerStreak streak = score.getStreak();
    PlayerStreak oldStreak = streak.copy();
    if (weapon == Weapon.RAILGUN) {
      streak.setConsequentRailHit(streak.getConsequentRailHit() + 1);
    }

    new GameStreakUpdateEvent(streak, oldStreak, player, game)
        .fire();
  }

  @EventHandler
  public void announceRailStreak(GameStreakUpdateEvent event) {
    Player p = event.getPlayer();
    int streak = event.getStreak().getConsequentRailHit();
    int oldStreak = event.getOldStreak().getConsequentRailHit();
    if (oldStreak == streak) return;
    if (streak > 2) {
      playSound(p, SoundConstants.IMPRESSIVE);
    }
  }

  @EventHandler
  public void announceFragStreak(GameStreakUpdateEvent event) {
    Player p = event.getPlayer();
    int streak = event.getStreak().getFragStreak();
    int oldStreak = event.getOldStreak().getFragStreak();
    if (oldStreak == streak) return;
    switch (streak) {
      case 0, 1, 2 -> { /* Don't announce */ }
      case 3, 4 -> playSound(p, SoundConstants.EXCELLENT);
      default -> playSound(p, SoundConstants.HOLYSHIT);
    }
  }

  private void playSound(Player p, String sound) {
    p.playSound(p, sound, SoundCategory.VOICE, .8f, 1f);
  }
}
