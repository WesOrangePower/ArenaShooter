package agency.shitcoding.arena.hologram;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

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
  public void onHologramClick(PlayerInteractAtEntityEvent event) {
    var player = event.getPlayer();
    var entity = event.getRightClicked();
    if (entity.getType() == EntityType.TEXT_DISPLAY) {
      for (var hologram : holograms) {
        if (hologram.getEntity().equals(entity)) {
          event.setCancelled(true);
          if (hologram.getOnClick() != null) {
            hologram.getOnClick().accept(player);
          }
          return;
        }
      }
    }
  }

  private HologramListener() {}
}
