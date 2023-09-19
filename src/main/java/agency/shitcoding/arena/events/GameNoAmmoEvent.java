package agency.shitcoding.arena.events;

import agency.shitcoding.arena.gamestate.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameNoAmmoEvent extends GameEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Game gameByPlayer;
    private final Player player;

    public GameNoAmmoEvent(Game gameByPlayer, Player player) {
        super();
        this.gameByPlayer = gameByPlayer;
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
