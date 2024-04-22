package agency.shitcoding.arena.models;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Ammo {
  BULLETS(0, 200),
  SHELLS(1, 50),
  ROCKETS(2, 30),
  LIGHTNING(3, 600),
  CELLS(4, 200);

  public final int slot;
  public final int max;

  public static int[] getAmmoForPlayer(Player player) {
    int[] ammoValues = player.getPersistentDataContainer()
        .get(Keys.getPlayerAmmoKey(), PersistentDataType.INTEGER_ARRAY);
    if (ammoValues == null || ammoValues.length != Ammo.values().length) {
      ammoValues = new int[Ammo.values().length];
    }
    return ammoValues;
  }

  public static int getAmmoForPlayer(Player player, Ammo ammoType) {
    return getAmmoForPlayer(player)[ammoType.slot];
  }

  public static void setAmmoForPlayer(Player player, Ammo ammoType, int value) {
    int[] ammoValues = getAmmoForPlayer(player);
    ammoValues[ammoType.slot] = value;
    player.getPersistentDataContainer()
        .set(Keys.getPlayerAmmoKey(), PersistentDataType.INTEGER_ARRAY, ammoValues);
    displayAmmoActionBar(player);
  }

  public static void setAmmoForPlayer(Player player, int value) {
    int[] ammoValues = new int[Ammo.values().length];
    Arrays.fill(ammoValues, value);
    player.getPersistentDataContainer()
        .set(Keys.getPlayerAmmoKey(), PersistentDataType.INTEGER_ARRAY, ammoValues);
  }

  public static void maxAmmoForPlayer(Player player) {
    int[] ammoValues = new int[Ammo.values().length];
    for (Ammo ammo : Ammo.values()) {
      ammoValues[ammo.slot] = ammo.max;
    }
    player.getPersistentDataContainer()
        .set(Keys.getPlayerAmmoKey(), PersistentDataType.INTEGER_ARRAY, ammoValues);
  }

  public static final String[] AMMO_PICT = {
      "🔫", "💥", "🚀", "☢", "⚡"
  };
  public static final int[] AMMO_COLORS = {
      0xFFFF55, 0xFFAA00, 0xAA0000, 0xFFFFFF, 0x55FFFF
  };
  public static void displayAmmoActionBar(Player p) {
    int[] ammoValues = Ammo.getAmmoForPlayer(p);
    var builder = Component.text();
    boolean append = false;
    for (int i = 0; i < ammoValues.length; i++) {
      if (ammoValues[i] > 0) {
        if (append) {
          builder.append(Component.text("   ", NamedTextColor.GRAY));
        }
        builder.append(Component.text(AMMO_PICT[i] + " " + ammoValues[i],
            TextColor.color(AMMO_COLORS[i])));
        append = true;
      }
    }
    p.sendActionBar(builder);
  }
}
