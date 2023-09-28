package agency.shitcoding.arena.events;

import agency.shitcoding.arena.models.Ammo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
public class AmmoUpdateEvent extends GameEvent {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private int ammoDelta;
    private @Nullable Ammo ammo;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
