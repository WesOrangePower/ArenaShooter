package agency.shitcoding.arena.events;

import agency.shitcoding.arena.models.Weapon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class GameDamageEvent extends GameEvent {
    private static final HandlerList handlers = new HandlerList();
    private @Nullable Player dealer;
    private @NotNull LivingEntity victim;
    private double damage;
    private Weapon weapon;

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
