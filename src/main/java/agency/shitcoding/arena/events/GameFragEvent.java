package agency.shitcoding.arena.events;

import agency.shitcoding.arena.models.Weapon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameFragEvent extends GameEvent implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  private @Nullable Player killer;
  private Player victim;
  private @Nullable Weapon weapon;
  private final boolean isGibbed;
  private boolean cancelled;

  @SuppressWarnings("unused")
  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return getHandlerList();
  }

  public GameFragEvent(Player victim, @Nullable Player killer, @Nullable Weapon weapon, boolean isGibbed) {
    this.killer = killer;
    this.victim = victim;
    this.weapon = weapon;
    this.isGibbed = isGibbed;
  }
}
