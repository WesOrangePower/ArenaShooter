package agency.shitcoding.doublejump.models;

import agency.shitcoding.doublejump.DoubleJump;
import org.bukkit.NamespacedKey;

public class Keys {
    public static NamespacedKey getPlayerAmmoKey() {
        return new NamespacedKey(DoubleJump.getInstance(), "ammoValues");
    }
}
