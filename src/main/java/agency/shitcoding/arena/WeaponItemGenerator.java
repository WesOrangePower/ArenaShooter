package agency.shitcoding.arena;

import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class WeaponItemGenerator {

  public static ItemStack generate(Player player, Weapon weapon) {
    ItemStack item = new ItemStack(weapon.item, 1);
    var name = LangPlayer.of(player).getLocalized(weapon.name);
    item.editMeta(meta ->
        meta.displayName(Component.text(name, TextColor.color(weapon.color.asRGB()))));
    return item;
  }

  private WeaponItemGenerator() {
  }
}
