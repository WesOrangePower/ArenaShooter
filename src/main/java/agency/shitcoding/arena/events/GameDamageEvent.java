package agency.shitcoding.arena.events;

import agency.shitcoding.arena.models.Weapon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameDamageEvent extends GameEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private @Nullable Player dealer;
    private @NotNull LivingEntity victim;
    private double damage;
    private Weapon weapon;
    private boolean cancelled;

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public GameDamageEvent(@Nullable Player dealer, @NotNull LivingEntity victim, double damage, Weapon weapon) {
        this.dealer = dealer;
        this.victim = victim;
        this.damage = damage;
        this.weapon = weapon;
    }
}
