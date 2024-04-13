package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.Lobby;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class LobbyListener implements Listener {

  @EventHandler
  private void onMoveOutsideOfLobby(PlayerMoveEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
      return;
    }
    Location location = event.getPlayer().getLocation();
    Lobby lobby = Lobby.getInstance();
    if (Math.random() > .9
        && !lobby.getBoundaries().contains(location.toVector())
        && GameOrchestrator.getInstance().getGameByPlayer(event.getPlayer()).isEmpty()
    ) {
      lobby.sendPlayer(event.getPlayer());
    }
  }

  @EventHandler
  public void onGamemodeChangeToSpectatorOutsideOfAGame(PlayerGameModeChangeEvent event) {
    if (event.getNewGameMode() == GameMode.SPECTATOR
        && GameOrchestrator.getInstance().getGameByPlayer(event.getPlayer()).isEmpty()
    ) {
      Lobby.getInstance().sendPlayer(event.getPlayer());
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Lobby.getInstance().sendPlayer(event.getPlayer());
  }

}
