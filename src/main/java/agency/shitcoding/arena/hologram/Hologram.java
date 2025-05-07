package agency.shitcoding.arena.hologram;

import agency.shitcoding.arena.ArenaShooter;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@Getter
public final class Hologram {
  private TextDisplay entity;
  private @Nullable HologramAction onClick;
  private int lifetime;
  private @Nullable ScheduledTask task = null;

  public Hologram(TextDisplay entity, HologramAction onClick, int lifetime) {
    this.entity = entity;
    if (onClick != null) {
      setOnClick(onClick);
    }
    if (lifetime > 0) {
      setLifetime(lifetime);
    }
  }

  public void setOnClick(@Nullable HologramAction onClick) {
    this.onClick = onClick;
    if (onClick == null) {
      HologramListener.getInstance().removeHologram(this);
      return;
    }
    HologramListener.getInstance().addHologram(this);
  }

  public void setLifetime(@Range(from = 0, to = Integer.MAX_VALUE) int lifetime) {
    this.lifetime = lifetime;
    if (lifetime > 0) {
      this.task =
          Bukkit.getGlobalRegionScheduler()
              .runDelayed(
                  ArenaShooter.getInstance(),
                  (t) -> {
                    if (entity != null) {
                      entity.remove();
                      entity = null;
                    }
                  },
                  lifetime);
      return;
    }
    if (task != null) {
      task.cancel();
      task = null;
    }
  }
}
