package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.events.AmmoUpdateEvent;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.GameStage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AmmoListener implements Listener {

  @EventHandler
  public void onAmmoUpdate(AmmoUpdateEvent event) {
    Player player = event.getPlayer();
    Ammo ammo = event.getAmmo();
    if (ammo == null) {
      int[] ammoForPlayer = Ammo.getAmmoForPlayer(player);
      for (Ammo type : Ammo.values()) {
        int sum = Math.max(ammoForPlayer[type.slot] + event.getAmmoDelta(), 0);
        if (sum > type.max) {
          continue;
        }
        Ammo.setAmmoForPlayer(player, sum);
      }
      return;
    }
    int ammoForPlayer = Ammo.getAmmoForPlayer(player, ammo);
    int sum = Math.max(ammoForPlayer + event.getAmmoDelta(), 0);

    if (sum > ammo.max) {
      return;
    }
    if (GameOrchestrator.getInstance().getGameByPlayer(player)
        .map(g -> g.getGamestage() == GameStage.IN_PROGRESS).orElse(false)) {
      Ammo.setAmmoForPlayer(player, ammo, sum);
    }
  }
}
