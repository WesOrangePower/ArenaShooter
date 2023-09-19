package agency.shitcoding.doublejump;

import agency.shitcoding.doublejump.models.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WeaponItemGenerator {

    public static ItemStack generate(Player player, Weapon weapon) {
        ItemStack item = new ItemStack(weapon.item, 1);
        item.editMeta(meta -> {
            meta.displayName(Component.text(weapon.name, TextColor.color(weapon.color.asRGB())));
        });
        return item
    }
}
