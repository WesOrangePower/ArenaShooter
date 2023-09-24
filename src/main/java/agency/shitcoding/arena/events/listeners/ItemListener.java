package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.models.GameStage;
import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.LootPointInstance;
import agency.shitcoding.arena.models.Powerup;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Optional;

import static agency.shitcoding.arena.GameplayConstants.PROTECTION_POTION_EFFECT;
import static agency.shitcoding.arena.GameplayConstants.QUAD_DAMAGE_POTION_EFFECT;

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
            LootPointInstance lootPointInstance = game.getLootPoints().get(i);
            powerup = lootPointInstance.getLootPoint().getType();
        }

        boolean isPickedUp = powerup.getOnPickup().apply(player);

        if (isPickedUp) {
            player.sendRichMessage("<green><bold>Вы подобрали " + powerup.name());
            item.remove();
            if (i > 0) {
                game.getLootPoints().get(i).setLooted(true);
            }
        }
    }

    @EventHandler
    public void onEffectExpire(EntityPotionEffectEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) e.getEntity();


        if (e.getCause() != EntityPotionEffectEvent.Cause.EXPIRATION
                || e.getAction() != EntityPotionEffectEvent.Action.REMOVED
                || e.getOldEffect() == null) {
            return;
        }

        if (e.getOldEffect().getType().equals(QUAD_DAMAGE_POTION_EFFECT)) {
            GameOrchestrator.getInstance().getGameByPlayer(player)
                    .ifPresent(game -> game.getMajorBuffTracker().getQuadDamageTeam().removePlayer(player));
        }
        else if (e.getOldEffect().getType().equals(PROTECTION_POTION_EFFECT)) {
            GameOrchestrator.getInstance().getGameByPlayer(player)
                    .ifPresent(game -> game.getMajorBuffTracker().getProtectionTeam().removePlayer(player));
        }
    }
}
