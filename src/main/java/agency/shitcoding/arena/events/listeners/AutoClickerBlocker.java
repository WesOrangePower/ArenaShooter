package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.localization.LangPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AutoClickerBlocker implements Listener {
  private static final Logger logger = ArenaShooter.getInstance().getLogger();
  public static final int MAX_WARNINGS = 10;
  Map<Player, Long> lastShoot = new HashMap<>();
  Map<Player, Integer> warningCounter = new HashMap<>();

  private void onTrigger(Player player) {
    GameOrchestrator.getInstance().getGameByPlayer(player)
        .ifPresent(game -> {
          game.removePlayer(player);
          for (Player gamePlayer : game.getPlayers()) {
            LangPlayer.of(gamePlayer)
                .sendRichLocalized("game.autoclicker.kickPlayer", player.getName());
          }
        });
    player.playSound(player, Sound.ENTITY_GHAST_SCREAM, 1f, 1f);
    LangPlayer langPlayer = new LangPlayer(player);
    langPlayer.sendRichLocalized("game.autoclicker.kickSelf");
    logger.info("Kicked player " + player.getName() + " for autoclicking");
  }


  @EventHandler
  public void onShoot(GameShootEvent event) {
    var player = event.getParentEvent().getPlayer();
    var now = System.currentTimeMillis();
    var lastShootTime = lastShoot.put(player, now);
    var warning = warningCounter.getOrDefault(player, 0);
    if (lastShootTime != null && now - lastShootTime < 90) {
      warningCounter.put(player, warning + 1);
      if (warning > MAX_WARNINGS) {
        onTrigger(player);
      }
    } else {
      warningCounter.put(player, Math.max(0, warning - 1));
    }
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent event) {
    lastShoot.remove(event.getPlayer());
    warningCounter.remove(event.getPlayer());
  }
}
