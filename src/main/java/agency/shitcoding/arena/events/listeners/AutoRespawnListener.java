package agency.shitcoding.arena.events.listeners;

import static agency.shitcoding.arena.GameplayConstants.PROTECTION_POTION_EFFECT;
import static agency.shitcoding.arena.GameplayConstants.QUAD_DAMAGE_POTION_EFFECT;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.events.GameStreakUpdateEvent;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.Lobby;
import agency.shitcoding.arena.gamestate.PlayerScore;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.GameStage;
import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.Powerup;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class AutoRespawnListener implements Listener {

  private static void dropPowerup(Player p, Powerup powerup) {
    Location location = p.getLocation().toCenterLocation().clone();

    var item =
        location
            .getWorld()
            .dropItem(
                location,
                powerup.getItemStack(),
                i -> {
                  i.getPersistentDataContainer()
                      .set(Keys.getPowerupKey(), PersistentDataType.STRING, powerup.name());
                  i.setCanMobPickup(false);
                });
    item.setVelocity(new Vector(0f, .02f, 0f));
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    event.setCancelled(true);
    event.getDrops().clear();
    event.setNewExp(0);
    Ammo.setAmmoForPlayer(event.getPlayer(), 0);
    Player p = event.getPlayer();

    final Player killer = p.getKiller() == p ? null : p.getKiller();
    boolean killedThemselves = killer == null;

    GameOrchestrator.getInstance()
        .getGameByPlayer(p)
        .ifPresent(
            game -> {
              PotionEffect quadDamagePotionEffect = p.getPotionEffect(QUAD_DAMAGE_POTION_EFFECT);
              if (quadDamagePotionEffect != null) {
                game.getMajorBuffTracker().setQuadDamageTicks(quadDamagePotionEffect.getDuration());
                dropPowerup(p, Powerup.QUAD_DAMAGE);
              }

              PotionEffect protectionPotionEffect = p.getPotionEffect(PROTECTION_POTION_EFFECT);
              if (protectionPotionEffect != null) {
                game.getMajorBuffTracker().setProtectionTicks(protectionPotionEffect.getDuration());
                dropPowerup(p, Powerup.PROTECTION);
              }

              game.onPlayerDeath(p);
              if (killer != null) game.onKill(killer);
              if (game.getGamestage() != GameStage.IN_PROGRESS) {
                return;
              }
              resetStreak(p, game);
              if (killedThemselves) {
                game.updateScore(p, -1);
              } else {
                game.updateScore(killer, 1);
              }
            });

    p.clearActivePotionEffects();
    p.setGameMode(GameMode.SPECTATOR);
    var l = LangPlayer.of(p);
    Title title = getDeathTitle(l, killer);
    p.showTitle(title);

    Bukkit.getScheduler()
        .runTaskLater(
            ArenaShooter.getInstance(),
            () -> {
              Optional<Game> gameByPlayer = GameOrchestrator.getInstance().getGameByPlayer(p);
              gameByPlayer.ifPresentOrElse(
                  game -> game.getArena().spawn(p, game, game.getLootPointFilter()),
                  () -> Lobby.getInstance().sendPlayer(p));
            },
            60);
  }

  private static @NotNull Title getDeathTitle(LangPlayer l, Player killer) {
    String localized = l.getLocalized("game.death.title.title");
    if (l.getPlayer().getName().equals("markovav") && Math.random() > 0.95) {
      localized = "Bed destroyed";
      l.getPlayer().playSound(l.getPlayer(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1f, 1f);
    }
    return Title.title(
        Component.text(localized, NamedTextColor.RED),
        Component.text(
            killer == null
                ? l.getLocalized("game.death.title.self")
                : l.getLocalized("game.death.title.other", killer.getName()),
            NamedTextColor.YELLOW));
  }

  @EventHandler
  public void onPlayerRespawn(PlayerPostRespawnEvent event) {
    Player player = event.getPlayer();
    Optional<Game> gameByPlayer = GameOrchestrator.getInstance().getGameByPlayer(player);
    gameByPlayer.ifPresentOrElse(
        game -> game.getArena().spawn(player, game, game.getLootPointFilter()),
        () -> Lobby.getInstance().sendPlayer(player));
  }

  private void resetStreak(Player p, Game g) {
    PlayerScore score = g.getScore(p);
    if (score != null) {
      var oldStreak = score.getStreak().copy();
      score.getStreak().setConsequentRailHit(0);
      score.getStreak().setFragStreak(0);
      new GameStreakUpdateEvent(score.getStreak(), oldStreak, p, g).fire();
    }
  }
}
