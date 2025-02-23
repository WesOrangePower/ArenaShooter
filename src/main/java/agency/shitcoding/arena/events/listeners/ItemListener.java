package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.LootManager;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class ItemListener implements Listener {

  @EventHandler
  public void onItemPickup(PlayerAttemptPickupItemEvent event) {
    Item item = event.getItem();
    String s =
        item.getPersistentDataContainer().get(Keys.getLootPointKey(), PersistentDataType.STRING);
    if (s == null) {
      return;
    }
    event.setCancelled(true);
    Player player = event.getPlayer();
    Optional<Game> gameByPlayer = GameOrchestrator.getInstance().getGameByPlayer(player);
    if (gameByPlayer.isEmpty()) {
      return;
    }

    Game game = gameByPlayer.get();
    if (game.getGamestage() != GameStage.IN_PROGRESS) {
      return;
    }

    Powerup powerup;
    LootManager lootManager = game.getLootManager();
    assert lootManager != null;
    LootPointInstance lootPointInstance = lootManager.getLootPoints().get(s);
    powerup = lootPointInstance.getLootPoint().getType();

    boolean isPickedUp = powerup.getOnPickup().apply(player);

    if (isPickedUp) {
      player.playSound(player, powerup.getType().getSoundName(), .5f, 1f);
      var langPlayer = LangPlayer.of(player);
      var powerupName = langPlayer.getLocalized(powerup.getDisplayName());
      if (powerup.getType() == PowerupType.AMMO) {
        Ammo ammo = Powerup.getAmmo(powerup);
        if (ammo == null) {
          throw new IllegalStateException(
              "Ammo type not found for PowerupType.AMMO powerup " + powerup.name());
        }
        var color = "#" + Integer.toHexString(Ammo.AMMO_COLORS[ammo.slot]);
        powerupName =
            String.format("<color:%s>%s %s</color>", color, Ammo.AMMO_PICT[ammo.slot], powerupName);
      }
      langPlayer.sendRichLocalized("powerup.pickup.self", powerupName);
      if (powerup.getType() == PowerupType.MAJOR_BUFF) {
        handleMajorBuff(player, game, powerup);
      }
      item.remove();
      lootPointInstance.setLooted(true);
    }
  }

  private void handleMajorBuff(Player player, Game game, Powerup powerup) {
    for (Player gamePlayer : game.getPlayers()) {
      if (gamePlayer == player) {
        continue;
      }
      var lang = LangPlayer.of(gamePlayer);
      lang.sendRichLocalized(
          "powerup.pickup.other", player.getName(), lang.getLocalized(powerup.getDisplayName()));
    }
  }
}
