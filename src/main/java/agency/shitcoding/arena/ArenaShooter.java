package agency.shitcoding.arena;

import agency.shitcoding.arena.command.ArenaDeathMatchCommandInvoker;
import agency.shitcoding.arena.events.PortalListener;
import agency.shitcoding.arena.events.listeners.*;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.statistics.StatisticsService;
import agency.shitcoding.arena.statistics.StatisticsServiceImpl;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.github.yannicklamprecht.worldborder.plugin.PersistenceWrapper;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.Objects;

@Getter
public final class ArenaShooter extends JavaPlugin {

  private ArenaWorldBorderApi worldBorderApi;
  private StatisticsService statisticsService;

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
    new AmmoListener(), new NoAmmoListener(), new PlasmaListener(), new RailListener(), new BFG9KListener(),
    new RocketListener(), new LightningGunListener(), new ShotgunListener(), new GauntletListener(),
    new MachineGunListener(), new InstagibListener(), new ItemListener(), new GameStreakListener(),
    new PortalListener(), new AutoClickerBlocker(), new MessageListener()
    };

    for (Listener listener : listeners) {
      getServer().getPluginManager().registerEvents(listener, this);
    }
  }
}
