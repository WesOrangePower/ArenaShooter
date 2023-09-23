package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.models.GameStage;
import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.LootPointInstance;
import agency.shitcoding.arena.models.Powerup;
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
        Integer i = item.getPersistentDataContainer()
                .get(Keys.LOOT_POINT_KEY, PersistentDataType.INTEGER);
        if (i == null) {
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
        LootPointInstance lootPointInstance = game.getLootPoints().get(i);
        Powerup type = lootPointInstance.getLootPoint().getType();
        boolean isPickedUp = type.getOnPickup().apply(player);

        if (isPickedUp) {
            player.sendRichMessage("<green><bold>Вы подобрали " + type.name());
            item.remove();
            lootPointInstance.setLooted(true);
        }
    }
}
