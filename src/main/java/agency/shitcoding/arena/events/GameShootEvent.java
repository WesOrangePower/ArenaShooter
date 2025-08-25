package agency.shitcoding.arena.events;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.models.Weapon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameShootEvent extends GameEvent {

  private static final HandlerList handlers = new HandlerList();
  private final PlayerInteractEvent parentEvent;
  private final Game game;
  private final Weapon weapon;


  public GameShootEvent(PlayerInteractEvent playerInteractEvent, Game game, Weapon weapon) {
    this.parentEvent = playerInteractEvent;
    this.game = game;
    this.weapon = weapon;
  }

  @SuppressWarnings("unused")
  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
