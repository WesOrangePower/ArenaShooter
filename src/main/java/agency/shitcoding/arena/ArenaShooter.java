package agency.shitcoding.arena;

import agency.shitcoding.arena.command.ArenaDeathMatchCommandInvoker;
import agency.shitcoding.arena.events.PortalListener;
import agency.shitcoding.arena.events.listeners.AmmoListener;
import agency.shitcoding.arena.events.listeners.AutoClickerBlocker;
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
import agency.shitcoding.arena.statistics.StatisticsService;
import agency.shitcoding.arena.statistics.StatisticsServiceImpl;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.github.yannicklamprecht.worldborder.plugin.PersistenceWrapper;
import java.io.File;
import java.util.Objects;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Getter
public final class ArenaShooter extends JavaPlugin {

  private ArenaWorldBorderApi worldBorderApi = null;
  private StatisticsService statisticsService = null;

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

    initStatistics();

    RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider
        = getServer().getServicesManager().getRegistration(WorldBorderApi.class);
    if (worldBorderApiRegisteredServiceProvider == null) {
      getLogger().info("WorldBorderApi not found. Cannot use red screen");
      return;
    }
    worldBorderApi = new ArenaWorldBorderApi(
        (PersistenceWrapper) worldBorderApiRegisteredServiceProvider.getProvider());
  }

  private void initStatistics() {
    //noinspection ResultOfMethodCallIgnored
    getDataFolder().mkdirs();
    var stats = new File(getDataFolder(), "stats.csv");
    statisticsService = new StatisticsServiceImpl(stats);
  }

  @Override
  public void onDisable() {
    for (Game game : GameOrchestrator.getInstance()
        .getGames()) {
      game.endGame("game.end.shutdown", false);
    }
  }

  private void registerListeners() {
    var listeners = new Listener[] {
    new AutoRespawnListener(), new BlockerListener(), new DamageListener(), new InteractListener(),
    new LobbyListener(), new InteractListener(), new LobbyListener(), new MovementListener(),
    new AmmoListener(), new NoAmmoListener(), new PlasmaListener(), new RailListener(),
    new RocketListener(), new LightningGunListener(), new ShotgunListener(), new GauntletListener(),
    new MachineGunListener(), new InstagibListener(), new ItemListener(), new GameStreakListener(),
    new PortalListener(), new AutoClickerBlocker()
    };

    for (Listener listener : listeners) {
      getServer().getPluginManager().registerEvents(listener, this);
    }
  }
}
