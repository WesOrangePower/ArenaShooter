package agency.shitcoding.arena;

import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

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

    String name;
    if (!isDefault) {
      CosmeticsService cosmetics = CosmeticsService.getInstance();
      String translatableName = Optional.ofNullable(cosmetics.getWeaponModName(player, weapon))
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

  private WeaponItemGenerator() {}
}
