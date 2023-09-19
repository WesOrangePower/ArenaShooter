package agency.shitcoding.doublejump.models;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Ammo {
    BULLETS(0, 200),
    SHELLS(1, 50),
    ROCKETS(2, 30),
    CELLS(3, 100);

    public final int slot;
    public final int max;

    static int[] getAmmoForPlayer(Player player) {
        int[] ammoValues = player.getPersistentDataContainer()
                .get(Keys.getPlayerAmmoKey(), PersistentDataType.INTEGER_ARRAY);
        if (ammoValues == null) {
            ammoValues = new int[Ammo.values().length];
        }
        return ammoValues;
    }
    static int getAmmoForPlayer(Player player, Ammo ammoType) {
        return getAmmoForPlayer(player)[ammoType.slot];
    }
    static void setAmmoForPlayer(Player player, Ammo ammoType, int value) {
        int[] ammoValues = getAmmoForPlayer(player);
        ammoValues[ammoType.slot] = value;
        player.getPersistentDataContainer()
                .set(Keys.getPlayerAmmoKey(), PersistentDataType.INTEGER_ARRAY, ammoValues);
    }
    static void setAmmoForPlayer(Player player, int value) {
        int[] ammoValues = new int[Ammo.values().length];
        Arrays.fill(ammoValues, value);
        player.getPersistentDataContainer()
                .set(Keys.getPlayerAmmoKey(), PersistentDataType.INTEGER_ARRAY, ammoValues);
    }
    static void maxAmmoForPlayer(Player player) {
        int[] ammoValues = new int[Ammo.values().length];
        for (Ammo ammo : Ammo.values()) {
            ammoValues[ammo.slot] = ammo.max;
        }
        player.getPersistentDataContainer()
                .set(Keys.getPlayerAmmoKey(), PersistentDataType.INTEGER_ARRAY, ammoValues);
    }
}
