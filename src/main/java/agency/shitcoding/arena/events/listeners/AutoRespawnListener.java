package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.events.GameStreakUpdateEvent;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.Lobby;
import agency.shitcoding.arena.gamestate.PlayerScore;
import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.GameStage;
import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.Powerup;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Optional;

import static agency.shitcoding.arena.GameplayConstants.PROTECTION_POTION_EFFECT;
import static agency.shitcoding.arena.GameplayConstants.QUAD_DAMAGE_POTION_EFFECT;

public class AutoRespawnListener implements Listener {
    private static void dropPowerup(Player p, ItemStack itemStack) {
        Location location = p.getLocation().toCenterLocation().clone();

        var item = location.getWorld().dropItem(location,
                itemStack,
                i -> {
                    i.getPersistentDataContainer().set(
                            Keys.LOOT_POINT_KEY,
                            PersistentDataType.INTEGER,
                            -1
                    );
                    i.setCanMobPickup(false);
                }
        );
        item.setVelocity(new Vector(0f, .2f, 0f));
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

        GameOrchestrator.getInstance().getGameByPlayer(p)
                .ifPresent(
                        game -> {
                            game.getMajorBuffTracker().getQuadDamageTeam().removePlayer(p);
                            game.getMajorBuffTracker().getProtectionTeam().removePlayer(p);

                            PotionEffect quadDamagePotionEffect = p.getPotionEffect(QUAD_DAMAGE_POTION_EFFECT);
                            if (quadDamagePotionEffect != null) {
                                game.getMajorBuffTracker().setQuadDamageTicks(quadDamagePotionEffect.getDuration());
                                dropPowerup(p, Powerup.QUAD_DAMAGE.getItemStack());
                            }

                            PotionEffect protectionPotionEffect = p.getPotionEffect(PROTECTION_POTION_EFFECT);
                            if (protectionPotionEffect != null) {
                                game.getMajorBuffTracker().setProtectionTicks(protectionPotionEffect.getDuration());
                                dropPowerup(p, Powerup.PROTECTION.getItemStack());
                            }

                            game.onPlayerDeath(p);
                            if (game.getGamestage() != GameStage.IN_PROGRESS) {
                                return;
                            }
                            resetStreak(p, game);
                            resetStreak(killer, game);
                            if (killedThemselves) {
                                game.getPlayers().forEach(pl -> pl.sendRichMessage("<red>" + p.getName() + "<gold> не справился с управлением."));
                                game.getScoreboardObjective().getScore(p).setScore(game.getOptScore(p).map(PlayerScore::getScore).orElse(0));
                            } else {
                                game.getPlayers().forEach(pl -> pl.sendRichMessage("<red>" + killer.getName() + "<gold> затраллил <red>" + p.getName() + "<gold> насмерть."));
                                game.recalculateScore(killer, 1);
                                game.getScoreboardObjective().getScore(killer).setScore(game.getOptScore(killer).map(PlayerScore::getScore).orElse(0));
                            }
                        }
                );

        p.clearActivePotionEffects();
        p.setGameMode(GameMode.SPECTATOR);
        p.showTitle(Title.title(
                Component.text("Полный развал", NamedTextColor.RED),
                Component.text(killer == null
                        ? "по собственной глупости"
                        : "от рук " + killer.getName(), NamedTextColor.YELLOW)
        ));

        Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), () -> {
            Optional<Game> gameByPlayer = GameOrchestrator.getInstance().getGameByPlayer(p);
            gameByPlayer.ifPresentOrElse(game -> game.getArena().spawn(p, game), () -> Lobby.getInstance().sendPlayer(p));
        }, 60);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent event) {
        Player player = event.getPlayer();
        Optional<Game> gameByPlayer = GameOrchestrator.getInstance().getGameByPlayer(player);
        gameByPlayer.ifPresentOrElse(game -> game.getArena().spawn(player, game), () -> Lobby.getInstance().sendPlayer(player));
    }

    private void resetStreak(Player p, Game g) {
        PlayerScore score = g.getScore(p);
        if (score != null) {
            score.getStreak().setConsequentRailHit(0);
            score.getStreak().setFragStreak(0);
            new GameStreakUpdateEvent(score.getStreak(), p, g)
                    .fire();
        }
    }
}
