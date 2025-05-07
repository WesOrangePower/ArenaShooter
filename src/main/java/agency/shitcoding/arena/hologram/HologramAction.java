package agency.shitcoding.arena.hologram;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

@FunctionalInterface
public interface HologramAction extends Consumer<Player> {}
