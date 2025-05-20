package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.models.StoredHologram;

import java.util.Collection;

public interface HologramStorage {
  Collection<StoredHologram> getHolograms();
  void reload();
}
