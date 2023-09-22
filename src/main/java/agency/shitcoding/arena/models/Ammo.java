package agency.shitcoding.arena.models;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.Arrays;

@RequiredArgsConstructor
public enum Ammo {
    BULLETS(0, 200),
    SHELLS(1, 50),
    ROCKETS(2, 30),
    CELLS(3, 100);

    public final int slot;
    public final int max;

    public static int[] getAmmoForPlayer(Player player) {
        int[] ammoValues = player.getPersistentDataContainer()
                .get(Keys.getPlayerAmmoKey(), PersistentDataType.INTEGER_ARRAY);
        if (ammoValues == null) {
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

        player.sendRichMessage("<green>[<yellow>" +
                ammoValues[0] + "<green> | <gold>" +
                ammoValues[1] + "<green> | <red>" +
                ammoValues[2] + "<green> | <aqua>" +
                ammoValues[3] + "<green>]"
                );
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
}
