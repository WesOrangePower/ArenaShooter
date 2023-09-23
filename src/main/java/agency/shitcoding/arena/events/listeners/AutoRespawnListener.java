package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.Lobby;
import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.GameStage;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Optional;

public class AutoRespawnListener implements Listener {
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
                            game.onPlayerDeath(p);
                            if (game.getGamestage() != GameStage.IN_PROGRESS) {
                                return;
                            }
                            if (killedThemselves) {
                                game.getScores().put(p, game.getScores().get(p) - 1);
                            } else {
                                game.getScores().put(killer, game.getScores().get(killer) + 1);
                            }
                        }
                );

        p.setGameMode(GameMode.SPECTATOR);
        p.showTitle(Title.title(
                Component.text("Ты развалился лол", NamedTextColor.RED),
                Component.text("Руками " + (killer == null ? "своими" : killer.getName() + "а"), NamedTextColor.YELLOW)
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
}
