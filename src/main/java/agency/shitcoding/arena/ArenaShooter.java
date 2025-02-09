package agency.shitcoding.arena;

import agency.shitcoding.arena.command.ArenaDeathMatchCommandInvoker;
import agency.shitcoding.arena.events.PortalListener;
import agency.shitcoding.arena.events.listeners.*;
import agency.shitcoding.arena.events.listeners.protocol.AnvilTextInputPacketAdapter;
import agency.shitcoding.arena.gamestate.CleanUp;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.announcer.AnnouncementSkipProvider;
import agency.shitcoding.arena.gamestate.announcer.AnnouncerConstant;
import agency.shitcoding.arena.gamestate.announcer.HardcodedStaticAnnouncementSkipProvider;
import agency.shitcoding.arena.statistics.StatisticsService;
import agency.shitcoding.arena.statistics.StatisticsServiceImpl;
import agency.shitcoding.arena.storage.CosmeticsUpdater;
import agency.shitcoding.arena.storage.MigrationDetector;
import agency.shitcoding.arena.storage.skips.YamlSkipProvider;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.github.yannicklamprecht.worldborder.plugin.PersistenceWrapper;
import java.io.File;
import java.util.Objects;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ArenaShooter extends JavaPlugin {

  private ArenaWorldBorderApi worldBorderApi;
  private StatisticsService statisticsService;
  private ProtocolManager protocolManager = null;

  private static ArenaShooter plugin;

  public static ArenaShooter getInstance() {
    return plugin;
  }

  @Override
  public void onEnable() {
    plugin = this;
    CleanUp.onStart();
    new MigrationDetector().detectMigration();
    registerListeners();

    Objects.requireNonNull(getCommand("arenaDeathMatch".toLowerCase()))
        .setExecutor(ArenaDeathMatchCommandInvoker.getInstance());

    GameOrchestrator.getInstance().unregisterScoreboard();

    initStatistics();

    initSchedulers();

    RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider =
        getServer().getServicesManager().getRegistration(WorldBorderApi.class);
    if (worldBorderApiRegisteredServiceProvider == null) {
      getLogger().info("WorldBorderApi not found. Cannot use red screen");
      return;
    }
    worldBorderApi =
        new ArenaWorldBorderApi(
            (PersistenceWrapper) worldBorderApiRegisteredServiceProvider.getProvider());
    Bukkit.getScheduler()
        .runTaskLater(
            this,
            () -> {
              protocolManager = ProtocolLibrary.getProtocolManager();
              if (isProtocolLibEnabled()) {
                protocolManager.addPacketListener(new AnvilTextInputPacketAdapter());
              } else {
                getLogger().info("ProtocolLib not found. Cannot use anvil text input");
              }
            },
            20L * 5);

    setupAnnouncer();
  }

  private void setupAnnouncer() {
    var dataFile = getDataFolder();
    if (!dataFile.exists()) {
      //noinspection ResultOfMethodCallIgnored
      dataFile.mkdirs();
    }
    var dataPath = dataFile.toPath();
    var file = dataPath.resolve("sound_skips.yaml").toFile();
    if (!file.exists()) file = dataPath.resolve("sound_skips.yml").toFile();
    AnnouncementSkipProvider skipProvider;
    if (file.exists()) {
      skipProvider = new YamlSkipProvider(file);
    } else {
      getLogger().warning("No announcer skips found, using default values");
      skipProvider = new HardcodedStaticAnnouncementSkipProvider();
    }
    getLogger().info(() -> "Using " + skipProvider.getClass().getSimpleName() + " for announcer skips.");
    AnnouncerConstant.setAnnouncementSkipProvider(skipProvider);
  }

  private void initSchedulers() {
    getServer().getScheduler().runTaskTimer(this, CosmeticsUpdater::refresh, 20L * 60, 20L * 60);
  }

  private void initStatistics() {
    //noinspection ResultOfMethodCallIgnored
    getDataFolder().mkdirs();
    var stats = new File(getDataFolder(), "stats.csv");
    statisticsService = new StatisticsServiceImpl(stats);
  }

  @Override
  public void onDisable() {
    getLogger().info("Shutting down");

    for (Game game : GameOrchestrator.getInstance().getGames()) {
      game.endGame("game.end.shutdown", false);
    }

    CleanUp.onShutdown();
  }

  public boolean isProtocolLibEnabled() {
    return protocolManager != null;
  }

  private void registerListeners() {
    var listeners =
        new Listener[] {
          new AutoRespawnListener(),
          new BlockerListener(),
          new DamageListener(),
          new InteractListener(),
          new LobbyListener(),
          new InteractListener(),
          new LobbyListener(),
          new MovementListener(),
          new AmmoListener(),
          new NoAmmoListener(),
          new PlasmaListener(),
          new RailListener(),
          new BFG9KListener(),
          new RocketListener(),
          new LightningGunListener(),
          new ShotgunListener(),
          new GauntletListener(),
          new MachineGunListener(),
          new InstagibListener(),
          new ItemListener(),
          new GameStreakListener(),
          new PortalListener(),
          new AutoClickerBlocker(),
          new MessageListener(),
          new DoorTriggerListener(),
          new CTFFlagListener(),
        };

    for (Listener listener : listeners) {
      getServer().getPluginManager().registerEvents(listener, this);
    }
  }
}
