package agency.shitcoding.doublejump.events.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import lombok.Getter;
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
        Player player = event.getPlayer();
        if (flyingPlayers.contains(player)) {
            flyingPlayers.remove(player);
            player.setAllowFlight(false);
        }
        airbornePlayers.remove(player);
    }


    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.setAllowFlight(true);
        flyingPlayers.add(player);
    }

    @EventHandler
    public void enableFlyOnPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();

        if (!flyingPlayers.contains(player)) {
            flyingPlayers.add(player);
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void playerOnGroundMove(PlayerMoveEvent event) {
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
