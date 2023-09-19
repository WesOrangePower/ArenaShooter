package agency.shitcoding.doublejump;

import agency.shitcoding.doublejump.command.ArenaDeathMatchCommandInvoker;
import agency.shitcoding.doublejump.events.listeners.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Set;

public final class DoubleJump extends JavaPlugin {
    MovementListener movementListener;
    RailListener railListener;

    public static Plugin getInstance() {
        return getPlugin(DoubleJump.class);
    }

    @Override
    public void onEnable() {
        movementListener = new MovementListener();
        railListener = new RailListener();
        getServer().getPluginManager().registerEvents(movementListener, this);
        getServer().getPluginManager().registerEvents(railListener, this);
        getServer().getPluginManager().registerEvents(new RocketListener(), this);
        getServer().getPluginManager().registerEvents(new AutoRespawnListener(), this);
        getServer().getPluginManager().registerEvents(new ShotgunListener(), this);

        Objects.requireNonNull(getCommand("arenaDeathMatch".toLowerCase()))
                .setExecutor(ArenaDeathMatchCommandInvoker.getInstance());
    }

    @Override
    public void onDisable() {
        Set<Player> flyingPlayers = movementListener.getFlyingPlayers();

        for (Player player : flyingPlayers) {
            player.setAllowFlight(false);
        }

        flyingPlayers.clear();
    }

}
