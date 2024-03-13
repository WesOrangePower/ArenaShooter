package agency.shitcoding.arena;

import agency.shitcoding.arena.command.ArenaDeathMatchCommandInvoker;
import agency.shitcoding.arena.events.PortalListener;
import agency.shitcoding.arena.events.listeners.AmmoListener;
import agency.shitcoding.arena.events.listeners.AutoRespawnListener;
import agency.shitcoding.arena.events.listeners.BlockerListener;
import agency.shitcoding.arena.events.listeners.DamageListener;
import agency.shitcoding.arena.events.listeners.GameStreakListener;
import agency.shitcoding.arena.events.listeners.GauntletListener;
import agency.shitcoding.arena.events.listeners.InstagibListener;
import agency.shitcoding.arena.events.listeners.InteractListener;
import agency.shitcoding.arena.events.listeners.ItemListener;
import agency.shitcoding.arena.events.listeners.LightningGunListener;
import agency.shitcoding.arena.events.listeners.LobbyListener;
import agency.shitcoding.arena.events.listeners.MachineGunListener;
import agency.shitcoding.arena.events.listeners.MovementListener;
import agency.shitcoding.arena.events.listeners.NoAmmoListener;
import agency.shitcoding.arena.events.listeners.PlasmaListener;
import agency.shitcoding.arena.events.listeners.RailListener;
import agency.shitcoding.arena.events.listeners.RocketListener;
import agency.shitcoding.arena.events.listeners.ShotgunListener;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.github.yannicklamprecht.worldborder.plugin.PersistenceWrapper;
import java.util.Objects;
import lombok.Getter;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Getter
public final class ArenaShooter extends JavaPlugin {

  private ArenaWorldBorderApi worldBorderApi = null;

  public static ArenaShooter getInstance() {
    return getPlugin(ArenaShooter.class);
  }


  @Override
  public void onEnable() {

    registerListeners();

    Objects.requireNonNull(getCommand("arenaDeathMatch".toLowerCase()))
        .setExecutor(ArenaDeathMatchCommandInvoker.getInstance());

    Scoreboard scoreboard = GameOrchestrator.getInstance().getScoreboard();
    scoreboard.getObjectives().forEach(Objective::unregister);
    scoreboard.getTeams().forEach(Team::unregister);

    RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider
        = getServer().getServicesManager().getRegistration(WorldBorderApi.class);
    if (worldBorderApiRegisteredServiceProvider == null) {
      getLogger().info("WorldBorderApi not found. Cannot use red screen");
      return;
    }
    worldBorderApi = new ArenaWorldBorderApi(
        (PersistenceWrapper) worldBorderApiRegisteredServiceProvider.getProvider());
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
    getServer().getPluginManager().registerEvents(new PlasmaListener(), this);
    getServer().getPluginManager().registerEvents(new RailListener(), this);
    getServer().getPluginManager().registerEvents(new RocketListener(), this);
    getServer().getPluginManager().registerEvents(new LightningGunListener(), this);
    getServer().getPluginManager().registerEvents(new ShotgunListener(), this);
    getServer().getPluginManager().registerEvents(new GauntletListener(), this);
    getServer().getPluginManager().registerEvents(new MachineGunListener(), this);
    getServer().getPluginManager().registerEvents(new InstagibListener(), this);
    getServer().getPluginManager().registerEvents(new ItemListener(), this);
    getServer().getPluginManager().registerEvents(new GameStreakListener(), this);
    getServer().getPluginManager().registerEvents(new PortalListener(), this);
  }
}
