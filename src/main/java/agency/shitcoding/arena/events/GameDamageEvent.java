package agency.shitcoding.doublejump.events;

import agency.shitcoding.doublejump.models.Weapon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class GameDamageEvent extends GameEvent {
    private  @Nullable Player dealer;
    private  @NotNull LivingEntity victim;
    private  double damage;
    private  Weapon weapon;

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
