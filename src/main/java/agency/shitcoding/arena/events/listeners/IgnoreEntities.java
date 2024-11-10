package agency.shitcoding.arena.events.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class IgnoreEntities {
  public static final NamespacedKey IGNORE_KEY = new NamespacedKey("arena", "ignoreHit");
  public static boolean shouldIgnoreEntity(@Nullable Entity entity) {
    if (entity == null) {
      return false;
    }
    if (!entity.getPersistentDataContainer().has(IGNORE_KEY, PersistentDataType.BOOLEAN)) {
      return false;
    }
    return Boolean.TRUE.equals(
        entity.getPersistentDataContainer().get(IGNORE_KEY, PersistentDataType.BOOLEAN));
  }
}
