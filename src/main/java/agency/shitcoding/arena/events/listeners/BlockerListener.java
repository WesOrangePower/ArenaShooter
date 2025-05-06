package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.events.listeners.protocol.AnvilTextInputPacketAdapter;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.team.GameTeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.models.Keys;
import lombok.val;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.persistence.PersistentDataType;

public class BlockerListener implements Listener {

  @EventHandler
  private void onDisconnect(PlayerQuitEvent event) {
    GameOrchestrator.getInstance()
        .getGameByPlayer(event.getPlayer())
        .ifPresent(game -> game.removePlayer(event.getPlayer()));
    AnvilTextInputPacketAdapter.getCurrentInput().remove(event.getPlayer());
  }

  @EventHandler
  private void onLeftClick(PlayerInteractEvent event) {
    if (event.getPlayer().getGameMode() == GameMode.ADVENTURE && event.getAction().isLeftClick()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void onHandSwap(PlayerSwapHandItemsEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  private void onDrop(PlayerDropItemEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  private void onFireballReflect(EntityDamageEvent event) {
    if (event.getEntity() instanceof Fireball) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void makeInvulnerableEntitiesUnkillable(EntityDeathEvent event) {
    if (event.getEntity().isInvulnerable()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void onInventoryOpen(InventoryOpenEvent event) {
    HumanEntity entity = event.getPlayer();
    if (!(entity instanceof Player player)) {
      return;
    }
    boolean isInGame = GameOrchestrator.getInstance().getGameByPlayer(player).isPresent();
    if (isInGame) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void onInventoryInteract(InventoryInteractEvent event) {
    HumanEntity entity = event.getWhoClicked();
    if (!(entity instanceof Player player)) {
      return;
    }
    boolean isInGame = GameOrchestrator.getInstance().getGameByPlayer(player).isPresent();
    if (isInGame) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void cancelDamageOutsideOfAGame(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player player) {
      GameOrchestrator gameOrchestrator = GameOrchestrator.getInstance();
      if (gameOrchestrator.getGameByPlayer(player).isEmpty()) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  private void cancelDamageByPlayerInSameTeam(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player damager
        && event.getEntity() instanceof Player victim) {
      Game game = GameOrchestrator.getInstance().getGameByPlayer(damager).orElse(null);
      if (game instanceof TeamGame teamGame) {
        var damagerTeam = teamGame.getTeamManager().getTeam(damager).map(GameTeam::getETeam);
        var victimTeam = teamGame.getTeamManager().getTeam(victim).map(GameTeam::getETeam);
        if (damagerTeam.equals(victimTeam)) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  private void cancelArmorDurabilityChange(PlayerItemDamageEvent event) {
    if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void onItemSwapToOffhand(PlayerSwapHandItemsEvent event) {
    if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void onItemHandCheck(PlayerSwapHandItemsEvent event) {
    if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void disableTrapdoors(PlayerInteractEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
      return;
    }

    if (event.getClickedBlock() != null
        && Tag.TRAPDOORS.isTagged(event.getClickedBlock().getType())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void disableGates(PlayerInteractEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
      return;
    }

    if (event.getClickedBlock() != null
        && Tag.FENCE_GATES.isTagged(event.getClickedBlock().getType())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void disableClayPots(PlayerInteractEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
      return;
    }

    if (event.getClickedBlock() != null
        && Tag.FLOWER_POTS.isTagged(event.getClickedBlock().getType())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void disablePaintingBreaking(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player player && player.getGameMode() != GameMode.ADVENTURE) {
      return;
    }

    if (event.getEntity().getType() == EntityType.PAINTING) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void disableKillCommand(PlayerCommandPreprocessEvent event) {
    if (event.getMessage().startsWith("/kill")
        || event.getMessage().startsWith("/minecraft:kill")) {
      event.getPlayer().sendRichMessage("<red>Gubami");
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void disableInventoryManipulation(InventoryClickEvent event) {
    if (event.getWhoClicked() instanceof Player player) {
      if (player.getGameMode() == GameMode.ADVENTURE) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  private void disableFoodLevelChange(FoodLevelChangeEvent event) {
    if (event.getEntity() instanceof Player player) {
      if (player.getGameMode() == GameMode.ADVENTURE) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  private void disableMarkedEntityDrops(EntityDeathEvent event) {
    Boolean disableDrop =
        event
            .getEntity()
            .getPersistentDataContainer()
            .get(Keys.noDropOnDeath(), PersistentDataType.BOOLEAN);

    if (Boolean.TRUE.equals(disableDrop)) {
      event.getDrops().clear();
      event.setDroppedExp(0);
    }
  }
}
