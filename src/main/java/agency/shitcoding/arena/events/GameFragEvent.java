package agency.shitcoding.arena.events;

import agency.shitcoding.arena.models.Weapon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameFragEvent extends GameEvent implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  private @Nullable Player killer;
  private @NotNull Player victim;
  private @Nullable Weapon weapon;
  private boolean cancelled;

  @SuppressWarnings("unused")
  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return getHandlerList();
  }

  public GameFragEvent(@NotNull Player victim, @Nullable Player killer, @Nullable Weapon weapon) {
    this.killer = killer;
    this.victim = victim;
    this.weapon = weapon;
  }
}
