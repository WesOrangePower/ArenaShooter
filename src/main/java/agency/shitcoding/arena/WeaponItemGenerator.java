package agency.shitcoding.arena;

import static org.bukkit.persistence.PersistentDataType.STRING;

import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.Weapon;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

public final class WeaponItemGenerator {

  public static ItemStack $default(Player player, Weapon weapon) {
    return generate(player, weapon, true);
  }

  public static ItemStack generate(Player player, Weapon weapon) {
    return generate(player, weapon, false);
  }

  public static ItemStack generateMod(Player player, Weapon weapon, String mod) {
    ItemStack item = new ItemStack(weapon.item, 1);
    String name = LangPlayer.of(player).getLocalized(weapon.translatableName + "." + mod);
    item.editMeta(meta ->
        meta.displayName(Component.text(name, TextColor.color(weapon.color.asRGB())))
    );
    return item;
  }

  private static ItemStack generate(Player player, Weapon weapon, boolean isDefault) {
    ItemStack item = new ItemStack(weapon.item, 1);

    PersistentDataContainer pdc = player.getPersistentDataContainer();

    String name;
    if (!isDefault) {
      String translatableName = Optional.ofNullable(pdc.get(Keys.ofWeapon(weapon), STRING))
          .map(s -> weapon.translatableName + "." + s)
          .orElse(weapon.translatableName);
      name = LangPlayer.of(player).getLocalized(translatableName);
    } else {
      name = LangPlayer.of(player).getLocalized(weapon.translatableName);
    }

    item.editMeta(meta ->
        meta.displayName(Component.text(name, TextColor.color(weapon.color.asRGB()))));
    return item;
  }

  private WeaponItemGenerator() {
  }
}
