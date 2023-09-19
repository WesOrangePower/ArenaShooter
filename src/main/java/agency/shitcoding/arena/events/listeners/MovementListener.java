package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.models.Weapon;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.potion.PotionEffect.INFINITE_DURATION;
import static org.bukkit.potion.PotionEffectType.JUMP;

@Getter
public class MovementListener implements Listener {
    Set<Player> flyingPlayers = new HashSet<>();
    Set<Player> airbornePlayers = new HashSet<>();

    @EventHandler
    public void disableFlyOnPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
            return;
        }
        Player player = event.getPlayer();
        if (flyingPlayers.contains(player)) {
            flyingPlayers.remove(player);
            player.setAllowFlight(false);
        }
        airbornePlayers.remove(player);
    }


    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
            return;
        }
        Player player = event.getPlayer();
        player.setAllowFlight(true);
        flyingPlayers.add(player);
    }

    @EventHandler
    public void playerMoveOutsideArena(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        GameOrchestrator.getInstance().getGameByPlayer(player).map(Game::getArena)
                .ifPresent(arena -> {
                    if (!arena.isInside(location)) {
                        if (player.getGameMode() == GameMode.ADVENTURE && Math.random() > .9) {
                            new GameDamageEvent(null, player, 100, Weapon.GAUNTLET)
                                    .fire();
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
        Player player = event.getPlayer();

        if (!flyingPlayers.contains(player)) {
            flyingPlayers.add(player);
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void playerOnGroundMove(PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()) {
            airbornePlayers.remove(player);
            player.setWalkSpeed(.2f);
            var effect = new PotionEffect(JUMP, INFINITE_DURATION, 2, false, false, false);
            player.addPotionEffect(effect);
        }
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
        if (airbornePlayers.contains(player)) {
            return;
        }
        airbornePlayers.add(player);

        Location loc = player.getLocation();
        Vector direction = loc.getDirection();
        direction.multiply(1.02);
        direction.setY(1.02);
        player.setVelocity(direction);

        loc.getWorld().spawnParticle(Particle.FLAME, loc, 10, 0.5, 0.5, 0.5, 0.1);
    }
}
