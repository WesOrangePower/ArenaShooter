package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.GameNoAmmoEvent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NoAmmoListener implements Listener {

  @EventHandler
  public void onNoAmmo(GameNoAmmoEvent event) {
    Player player = event.getPlayer();
    Location eyeLocation = player.getEyeLocation();
    Location inFace = eyeLocation.clone().add(eyeLocation.getDirection());
    World world = inFace.getWorld();
    world.spawnParticle(Particle.SMOKE_NORMAL, inFace, 1, 0, 0, 0, 0);
    player.playSound(player, SoundConstants.NOAMMO, .5f, 2f);
  }
}
