package agency.shitcoding.doublejump.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public abstract class GameEvent extends Event {

    public void fire() {
        Bukkit.getPluginManager().callEvent(this);
    }
}
