package agency.shitcoding.arena.events.listeners;

import static java.util.Objects.requireNonNull;

import agency.shitcoding.arena.gamestate.CTFGame;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.models.Keys;
import org.bukkit.GameMode;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.Nullable;

public class CTFFlagListener implements Listener {

  @EventHandler
  public void resetFlagOnOOB(ItemDespawnEvent event) {
    var item = event.getEntity();
    resetFlagByItem(item);
  }

  @EventHandler
  public void resetFlagOnItemDestroy(EntityDamageEvent event) {
    if (event.getEntity() instanceof Item item) {
      if (getTag(item) != null && item.getHealth() - event.getFinalDamage() <= 0) {
        resetFlagByItem(item);
      }
    }
  }

  @EventHandler
  public void handleFlagPickup(PlayerAttemptPickupItemEvent event) {
    var player = event.getPlayer();
    if (player.getGameMode() != GameMode.ADVENTURE) {
      return;
    }
    var item = event.getItem();
    var gameHash = getTag(item);
    if (gameHash == null) {
      return;
    }

    event.setCancelled(true);

    GameOrchestrator.getInstance()
        .getGameByPlayer(player)
        .ifPresent(
            game -> {
              if (game instanceof CTFGame ctfGame) {
                ctfGame.tryPickupFlag(player, item);
              }
            });
  }

  private @Nullable Integer getTag(Item item) {
    return item.getPersistentDataContainer().get(Keys.getFlagKey(), PersistentDataType.INTEGER);
  }

  private void resetFlagByItem(Item item) {
    var gameHash = getTag(item);
    if (gameHash == null) {
      return;
    }
    GameOrchestrator.getInstance()
        .getGameByHashCode(gameHash)
        .ifPresent(
            game -> {
              if (game instanceof CTFGame ctfGame) {
                requireNonNull(ctfGame.getFlagManager())
                    .getFlagByMaterial(item.getItemStack().getType())
                    .ifPresent(flag -> ctfGame.getFlagManager().reset(flag));
              }
            });
  }
}
