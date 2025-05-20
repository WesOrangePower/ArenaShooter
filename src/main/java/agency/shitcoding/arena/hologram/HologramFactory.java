package agency.shitcoding.arena.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class HologramFactory {
  public static Hologram hologram(@NotNull Component content, @NotNull Location location) {
    return hologram(content, location, null, 0);
  }

  public static Hologram hologram(
      @NotNull Component content,
      @NotNull Location location,
      @Range(from = 0, to = Integer.MAX_VALUE) int lifetime) {
    return hologram(content, location, null, lifetime);
  }

  public static Hologram hologram(
      @NotNull Component content, @NotNull Location location, @Nullable HologramAction onClick) {
    return hologram(content, location, onClick, 0);
  }

  public static Hologram hologram(
      @NotNull Component content,
      @NotNull Location location,
      @Nullable HologramAction onClick,
      @Range(from = 0, to = Integer.MAX_VALUE) int lifetime) {

    var entity =
        location
            .getWorld()
            .spawn(
                location,
                TextDisplay.class,
                textDisplay -> {
                  textDisplay.text(content);
                  textDisplay.setBillboard(Display.Billboard.CENTER);
                });

    return new Hologram(entity, onClick, lifetime);
  }
}
