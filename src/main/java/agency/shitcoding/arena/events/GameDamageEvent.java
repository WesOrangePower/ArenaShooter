package agency.shitcoding.arena.events;

import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.gamestate.team.TeamManager;
import agency.shitcoding.arena.models.Weapon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameDamageEvent extends GameEvent implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  private @Nullable Player dealer;
  private @NotNull LivingEntity victim;
  private double damage;
  private Weapon weapon;
  private boolean cancelled;

  @SuppressWarnings("unused")
  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }

  public GameDamageEvent(@Nullable Player dealer, @NotNull LivingEntity victim, double damage,
      Weapon weapon) {
    this.dealer = dealer;
    this.victim = victim;
    this.damage = damage;
    this.weapon = weapon;
  }


  @Override
  public void fire() {
    if (checkBeforeFire()) {
      Bukkit.getPluginManager().callEvent(this);
    }
  }


  private boolean checkBeforeFire() {
    if (victim.isDead() || cancelled) {
      return false;
    }
    if (dealer != null) {
      if (dealer.equals(victim)) {
        return true;
      }
      TeamGame game = GameOrchestrator.getInstance().getGameByPlayer(dealer)
          .filter(TeamGame.class::isInstance)
          .map(TeamGame.class::cast)
          .orElse(null);
      if (game != null && victim instanceof Player victimPlayer) {
        TeamManager teamManager = game.getTeamManager();
        return !teamManager.getTeam(dealer).equals(teamManager.getTeam(victimPlayer));
      }
    }
    return true;
  }
}
