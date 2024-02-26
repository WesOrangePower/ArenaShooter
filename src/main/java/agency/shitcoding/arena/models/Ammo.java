package agency.shitcoding.arena.models;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

  public static void displayAmmoActionBar(Player p) {
    int[] ammoValues = Ammo.getAmmoForPlayer(p);
    String richTextStr = "<yellow>" +
        ammoValues[0] + "<gray> | <gold>" +
        ammoValues[1] + "<gray> | <red>" +
        ammoValues[2] + "<gray> | <white>" +
        ammoValues[3] + "<gray> | <aqua>" +
        ammoValues[4] + "<gray>";
    p.sendActionBar(MiniMessage.miniMessage().deserialize(richTextStr));
  }
}
