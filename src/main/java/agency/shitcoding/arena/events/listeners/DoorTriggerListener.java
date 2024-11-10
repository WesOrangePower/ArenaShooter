package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.models.door.Door;
import agency.shitcoding.arena.models.door.DoorTrigger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class DoorTriggerListener implements Listener {

  @EventHandler
  public void tryRunDoorTrigger(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    var gameOpt = GameOrchestrator.getInstance().getGameByPlayer(player);
    if (gameOpt.isEmpty()) {
      return;
    }
    var game = gameOpt.get();

    game.getArena().getDoorTriggers().stream()
        .filter(trigger -> (trigger.getTriggerType() & DoorTrigger.INTERACTION) == 1)
        .filter(trigger -> trigger.getLocation().getBlock().equals(event.getClickedBlock()))
        .map(DoorTrigger::getDoorIds)
        .forEach(doorIds -> game.getArena().getDoors().stream()
            .filter(door -> doorIds.contains(door.getDoorId()))
            .forEach(Door::open)
        );
  }
}
