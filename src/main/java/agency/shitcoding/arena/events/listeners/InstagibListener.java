package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;

public class InstagibListener implements Listener {

  @EventHandler(priority = EventPriority.LOW)
  public void onDamage(GameDamageEvent event) {
    LivingEntity victim = event.getVictim();
    if (victim instanceof Player p) {
      Optional<Game> gameByPlayer = GameOrchestrator.getInstance().getGameByPlayer(p);
      if (gameByPlayer.isPresent()
          && gameByPlayer.get().getRuleSet() == RuleSet.INSTAGIB
          && event.getWeapon() == Weapon.RAILGUN
      ) {
        event.setDamage(event.getDamage() * 3);
      }
    }
  }
}
