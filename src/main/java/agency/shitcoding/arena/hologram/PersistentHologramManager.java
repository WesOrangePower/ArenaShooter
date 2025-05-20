package agency.shitcoding.arena.hologram;

import agency.shitcoding.arena.gamestate.CleanUp;
import agency.shitcoding.arena.storage.StorageProvider;

import java.util.List;
import java.util.stream.Collectors;

import static agency.shitcoding.arena.hologram.HologramFactory.hologram;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class PersistentHologramManager {
  private static PersistentHologramManager instance;

  private PersistentHologramManager() {
    spawn();
  }

  public static PersistentHologramManager getInstance() {
    if (instance == null) {
      instance = new PersistentHologramManager();
    }
    return instance;
  }

  private List<Hologram> holograms;

  public void killAll() {
    holograms.forEach(Hologram::remove);
    holograms.clear();
    CleanUp.removeOldHolograms();
  }

  public void reload() {
    killAll();
    spawn();
  }

  private void spawn() {
    this.holograms =
        StorageProvider.getHologramStorage().getHolograms().stream()
            .map(
                hologram ->
                    hologram(
                        miniMessage().deserialize(hologram.getText()),
                        hologram.getLocation(),
                        hologram.getCommand() == null ? null : (p) -> p.performCommand(hologram.getCommand()),
                        hologram.getLifetime()))
            .collect(Collectors.toList());
  }
}
