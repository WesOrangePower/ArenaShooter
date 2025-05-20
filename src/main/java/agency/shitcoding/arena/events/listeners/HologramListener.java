package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.hologram.Hologram;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class HologramListener implements Listener {
  private static HologramListener instance;
  public static HologramListener getInstance() {
    if (instance == null) {
      instance = new HologramListener();
    }
    return instance;
  }

  private final List<Hologram> holograms = new ArrayList<>();

  public void addHologram(Hologram hologram) {
    holograms.add(hologram);
  }

  public void removeHologram(Hologram hologram) {
    holograms.remove(hologram);
  }

  @EventHandler
  public void onHologramClick(PlayerInteractEvent event) {
    var player = event.getPlayer();
    var eyeLocation = player.getEyeLocation();
    var direction = eyeLocation.getDirection();
    var location = eyeLocation.clone();
    for (float i = 0f; i < 3.5f; i += .5f) {
      location.add(direction.clone().multiply(i));
      for (var hologram : holograms) {
        if (!hologram.getEntity().getLocation().getWorld().equals(location.getWorld())) {
          continue;
        }
        if (hologram.getBoundingBox().contains(location.toVector())) {
          Objects.requireNonNull(hologram.getOnClick()).accept(player);
          return;
        }
      }
    }
  }

  private HologramListener() {}
}
