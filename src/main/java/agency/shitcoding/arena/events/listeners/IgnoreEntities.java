package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.models.Keys;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class IgnoreEntities {
  public static final NamespacedKey IGNORE_HIT_KEY = Keys.getIgnoreHitKey();

  public static void ignoreEntity(Supplier<PersistentDataContainer> persistentDataContainerSupplier) {
    persistentDataContainerSupplier.get().set(IGNORE_HIT_KEY, PersistentDataType.BOOLEAN, true);
  }

  public static boolean shouldIgnoreEntity(@Nullable Entity entity) {
    if (entity == null) {
      return false;
    }
    if (entity.getType() == EntityType.PLAYER && ((Player)entity).getGameMode() != GameMode.ADVENTURE) {
      return true;
    }
    if (!entity.getPersistentDataContainer().has(IGNORE_HIT_KEY, PersistentDataType.BOOLEAN)) {
      return false;
    }
    return Boolean.TRUE.equals(
        entity.getPersistentDataContainer().get(IGNORE_HIT_KEY, PersistentDataType.BOOLEAN));
  }
}
