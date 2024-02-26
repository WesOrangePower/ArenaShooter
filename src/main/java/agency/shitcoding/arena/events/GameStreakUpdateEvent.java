package agency.shitcoding.arena.events;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.models.PlayerStreak;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public class GameStreakUpdateEvent extends GameEvent {

  private static final HandlerList handlers = new HandlerList();
  private final PlayerStreak streak;
  private final Player player;
  private final Game game;

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }

  @SuppressWarnings("unused")
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
