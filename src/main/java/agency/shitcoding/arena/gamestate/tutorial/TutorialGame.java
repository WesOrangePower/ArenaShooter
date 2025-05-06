package agency.shitcoding.arena.gamestate.tutorial;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.gamestate.DeathMatchGame;
import agency.shitcoding.arena.gamestate.announcer.HardcodedStaticAnnouncementSkipProvider;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.*;
import agency.shitcoding.arena.worlds.ArenaWorld;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TutorialGame extends DeathMatchGame implements Listener {
  protected LootPointFilter lootPointFilter = this::lootPointFilter;
  public int stage = 0;
  private Player player;
  private ScriptRunner scriptRunner;

  @Override
  public LootPointFilter getLootPointFilter() {
    return lootPointFilter;
  }

  public TutorialGame(ArenaWorld arenaWorld, RuleSet ruleSet, GameRules gameRules) {
    super(arenaWorld, ruleSet, gameRules);
  }

  public TutorialGame(ArenaWorld arenaWorld) {
    this(arenaWorld, RuleSet.TUTORIAL, RuleSet.TUTORIAL.getDefaultGameRules());
  }

  @Override
  protected void startGameStage2() {
    super.startGameStage2();
    ArenaScript script = new TutorialScript(
        player, this, new HardcodedStaticAnnouncementSkipProvider()
    );
    ScriptBuilder sb = new ScriptBuilder();
    script.build(sb);
    scriptRunner = new ScriptRunner(sb);
    scriptRunner.start();
    ArenaShooter plugin = ArenaShooter.getInstance();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @Override
  public void endGame(String reason, boolean intendedEnding, Object... toFormat) {
    super.endGame(reason, intendedEnding, toFormat);
    if (scriptRunner != null) {
      scriptRunner.close();
      scriptRunner = null;
    }
    HandlerList.unregisterAll(this);
  }

  @Override
  public void updateScore(Player p, int delta) {
    /*
    Score does not update on kills
    */
  }

  @Override
  public void addPlayer(Player player) {
    super.addPlayer(player);
    this.player = player;
  }

  private boolean lootPointFilter(LootPoint lootPoint, Player player) {
    return lootPoint.getMarkers() == LootPointMarker.TUTORIAL_MARKER.getValue() + stage;
  }

  @Override
  public void onPlayerDeath(Player player) {
    endGame("game.end.tutorialEnd", false);
  }

  @Override
  public void startGame() {
    super.startGame();
  }

  public ItemStack getExitItem(Player player) {
    var item = new ItemStack(Material.IRON_DOOR, 1);
    var langPlayer = LangPlayer.of(player);
    item.editMeta(meta -> meta.displayName(langPlayer.getRichLocalized("tutorial.exitItem.name")));
    return item;
  }

  @EventHandler
  public void handleExitItem(PlayerInteractEvent event) {
    var item = event.getItem();
    if (item != null && item.equals(getExitItem(event.getPlayer()))) {
      event.getPlayer().performCommand("arena leave");
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void disableSelfDamage(GameDamageEvent event) {
    if (event.getVictim().getName().equals(player.getName())) {
      event.setCancelled(true);
    }
  }
}
