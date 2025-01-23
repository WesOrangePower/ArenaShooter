package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.models.Weapon;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import static org.bukkit.potion.PotionEffect.INFINITE_DURATION;
import static org.bukkit.potion.PotionEffectType.JUMP;
import static org.bukkit.potion.PotionEffectType.WEAKNESS;

@Getter
public class MovementListener implements Listener {

  @EventHandler
  public void disableFlyOnPlayerQuit(PlayerQuitEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
      return;
    }
    Player player = event.getPlayer();
    player.setAllowFlight(false);
  }

  @EventHandler
  public void onPlayerSpawn(PlayerRespawnEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
      return;
    }
    Player player = event.getPlayer();
    player.setAllowFlight(true);
  }

  @EventHandler
  public void playerMoveOutsideArena(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    Location location = player.getLocation();
    GameOrchestrator.getInstance()
        .getGameByPlayer(player)
        .map(Game::getArena)
        .ifPresent(
            arena -> {
              if (!arena.isInside(location)) {
                if (player.getGameMode() == GameMode.ADVENTURE) {
                  new GameDamageEvent(null, player, 100, Weapon.GAUNTLET).fire();
                  return;
                }
                if (player.getGameMode() == GameMode.SPECTATOR) {
                  // midway between lower and upperbounds
                  Location lower = arena.getLowerBound();
                  Location upper = arena.getUpperBound();
                  double x = (lower.getX() + upper.getX()) / 2;
                  double y = (lower.getY() + upper.getY()) / 2;
                  double z = (lower.getZ() + upper.getZ()) / 2;
                  player.teleport(new Location(lower.getWorld(), x, y, z));
                }
              }
            });
  }

  @EventHandler
  public void enableFlyOnPlayerJump(PlayerJumpEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
      return;
    }
    event.getPlayer().setAllowFlight(true);
  }

  @EventHandler
  public void playerOnGroundMove(PlayerMoveEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
      return;
    }
    Player player = event.getPlayer();
    if (player.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()) {
      player.setWalkSpeed(.4f);
      event.getPlayer().setAllowFlight(true);
      var effect = new PotionEffect(JUMP, INFINITE_DURATION, 2, false, false, false);
      player.addPotionEffect(effect);
      effect = new PotionEffect(WEAKNESS, INFINITE_DURATION, 10, false, false, false);
      player.addPotionEffect(effect);
    }
    GameOrchestrator.getInstance()
        .getGameByPlayer(player)
        .ifPresent(
            game ->
                game.getArena().getRamps().stream()
                    .filter(ramp -> ramp.isTouching(player))
                    .forEach(ramp -> ramp.apply(player)));
  }

  @EventHandler
  public void doubleJumpOnPlayerFlight(PlayerToggleFlightEvent event) {
    if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
      return;
    }
    Player player = event.getPlayer();
    if (!event.isFlying()) {
      return;
    }
    event.setCancelled(true);
    player.setFlying(false);
    player.setAllowFlight(false);

    Location loc = player.getLocation();
    Vector direction = loc.getDirection();
    direction.multiply(1.1);
    direction.setY(0.60);
    player.setVelocity(direction);

    loc.getWorld().spawnParticle(Particle.FLAME, loc, 10, 0.5, 0.5, 0.5, 0.1);
  }

  @EventHandler
  public void fallCanceller(EntityDamageEvent e) {
    if (e.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void spectatorTeleportCleanup(PlayerTeleportEvent event) {
    Player player = event.getPlayer();
    if (player.getGameMode() != GameMode.SPECTATOR) {
      return;
    }
    if (GameOrchestrator.getInstance().getGames().stream()
        .map(Game::getSpectators)
        .noneMatch(s -> s.contains(player))) {
      return;
    }
    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    player.activeBossBars().forEach(b -> b.removeViewer(player));
  }
}
