package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.LootPointInstance;
import agency.shitcoding.arena.models.Powerup;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public final class LootSpawnTask implements Runnable {
  private final Game game;
  private final LootPointInstance instance;

  @Override
  public void run() {
    {
      Powerup powerup = instance.getLootPoint().getType();
      if (powerup == Powerup.QUAD_DAMAGE
          && game.getMajorBuffTracker().getQuadDamageTicks() != null) {
        return;
      }
      if (powerup == Powerup.PROTECTION
          && game.getMajorBuffTracker().getProtectionTicks() != null) {
        return;
      }
      ItemStack itemStack = powerup.getItemStack().clone();
      itemStack.lore(List.of(Component.text(UUID.randomUUID().toString())));
      Location location = instance.getLootPoint().getLocation().toCenterLocation().clone();

      @SuppressWarnings("DataFlowIssue")
      Optional<Item> any =
          location.getNearbyEntities(.5, .5, .5).stream()
              .filter(e -> e.getType() == EntityType.DROPPED_ITEM)
              .map(Item.class::cast)
              .filter(
                  i ->
                      i.getPersistentDataContainer()
                              .has(Keys.getLootPointKey(), PersistentDataType.STRING)
                          && i.getPersistentDataContainer()
                              .get(Keys.getLootPointKey(), PersistentDataType.STRING)
                              .equals(instance.getLootPoint().getId()))
              .findAny();

      if (any.isEmpty()) instance.setLooted(true);

      if (!instance.isLooted()) return;

      any.ifPresent(Entity::remove);

      var item =
          location
              .getWorld()
              .dropItem(
                  location,
                  itemStack,
                  i -> {
                    var persistentDataContainer = i.getPersistentDataContainer();
                    persistentDataContainer.set(
                        Keys.getLootPointKey(),
                        PersistentDataType.STRING,
                        instance.getLootPoint().getId());
                    persistentDataContainer.set(
                        Keys.getPowerupKey(),
                        PersistentDataType.STRING,
                        instance.getLootPoint().getType().name());
                    i.setCanMobPickup(false);
                    i.setWillAge(false);
                    i.setUnlimitedLifetime(true);
                    i.setPickupDelay(3);
                  });
      item.setVelocity(new Vector(0f, .2f, 0f));

      instance.setLooted(false);
    }
  }
}
