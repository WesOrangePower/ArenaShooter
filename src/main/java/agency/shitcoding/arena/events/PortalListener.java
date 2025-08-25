package agency.shitcoding.arena.events;

import agency.shitcoding.arena.gamestate.GameOrchestrator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PortalListener implements Listener {

  @EventHandler
  public void onPortalEnter(PlayerMoveEvent e) {
    Player player = e.getPlayer();
    GameOrchestrator.getInstance()
        .getGameByPlayer(player)
        .flatMap(
            game ->
                game.getArena().getPortals().stream()
                    .filter(
                        portal ->
                            portal
                                .getBoundingBox()
                                .contains(player.getLocation().toCenterLocation().toVector()))
                    .findFirst())
        .ifPresent(portal -> portal.teleport(player));
  }
}
