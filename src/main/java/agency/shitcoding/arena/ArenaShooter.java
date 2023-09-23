package agency.shitcoding.arena;

import agency.shitcoding.arena.command.ArenaDeathMatchCommandInvoker;
import agency.shitcoding.arena.events.listeners.*;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ArenaShooter extends JavaPlugin {

    public static ArenaShooter getInstance() {
        return getPlugin(ArenaShooter.class);
    }


    @Override
    public void onEnable() {

        registerListeners();

        Objects.requireNonNull(getCommand("arenaDeathMatch".toLowerCase()))
                .setExecutor(ArenaDeathMatchCommandInvoker.getInstance());
    }

    @Override
    public void onDisable() {
        for (Game game : GameOrchestrator.getInstance()
                .getGames()) {
            game.endGame("Server shutdown");
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AutoRespawnListener(), this);
        getServer().getPluginManager().registerEvents(new BlockerListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new InteractListener(), this);
        getServer().getPluginManager().registerEvents(new LobbyListener(), this);
        getServer().getPluginManager().registerEvents(new InteractListener(), this);
        getServer().getPluginManager().registerEvents(new LobbyListener(), this);
        getServer().getPluginManager().registerEvents(new MovementListener(), this);
        getServer().getPluginManager().registerEvents(new AmmoListener(), this);
        getServer().getPluginManager().registerEvents(new NoAmmoListener(), this);
        getServer().getPluginManager().registerEvents(new RailListener(), this);
        getServer().getPluginManager().registerEvents(new RocketListener(), this);
        getServer().getPluginManager().registerEvents(new ShotgunListener(), this);
        getServer().getPluginManager().registerEvents(new GauntletListener(), this);
        getServer().getPluginManager().registerEvents(new MachineGunListener(), this);
        getServer().getPluginManager().registerEvents(new InstagibListener(), this);
        getServer().getPluginManager().registerEvents(new ItemListener(), this);
    }

}
