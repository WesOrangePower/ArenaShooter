package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.LootManager;
import agency.shitcoding.arena.gamestate.LootManagerProvider;
import agency.shitcoding.arena.models.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
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

        Powerup powerup;
        if (i < 0) {
            Optional<Powerup> first = Arrays.stream(Powerup.values())
                    .filter(p -> p.getItemStack().getType() == event.getItem().getItemStack().getType())
                    .findFirst();
            if (first.isEmpty()) return;
            powerup = first.get();
        } else {
            LootManager lootManager = LootManagerProvider.get(game.getArena()).orElseThrow();
            LootPointInstance lootPointInstance = lootManager.getLootPoints().get(i);
            powerup = lootPointInstance.getLootPoint().getType();
        }

        boolean isPickedUp = powerup.getOnPickup().apply(player);

        if (isPickedUp) {
            player.playSound(player, powerup.getType().getSoundName(), .5f, 1f);
            player.sendRichMessage("<green><bold>Вы подобрали " + powerup.getDisplayName());
            if (powerup.getType() == PowerupType.MAJOR_BUFF) {
                handleMajorBuff(player, game, powerup);
            }
            item.remove();
            if (i > 0) {
                LootManager lootManager = LootManagerProvider.get(game.getArena()).orElseThrow();
                LootPointInstance lootPointInstance = lootManager.getLootPoints().get(i);
                lootPointInstance.setLooted(true);
            }
        }
    }

    private void handleMajorBuff(Player player, Game game, Powerup powerup) {
        for (Player gamePlayer : game.getPlayers()) {
            if (gamePlayer == player) continue;
            gamePlayer.sendRichMessage(
                    String.format("<red>%s подобрал %s!", player.getName(), powerup.getDisplayName())
            );
        }
    }
}
