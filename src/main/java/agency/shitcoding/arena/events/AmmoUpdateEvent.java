package agency.shitcoding.arena.events;

import agency.shitcoding.arena.models.Ammo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.Nullable;

@AllArgsConstructor
@Getter
public class AmmoUpdateEvent extends GameEvent {

  private static final HandlerList handlers = new HandlerList();
  private Player player;
  private int ammoDelta;
  private @Nullable Ammo ammo;

  @SuppressWarnings("unused")
  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
