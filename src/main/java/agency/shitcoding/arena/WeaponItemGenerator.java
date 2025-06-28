package agency.shitcoding.arena;

import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Weapon;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

  @SuppressWarnings("UnstableApiUsage")
  private static ItemStack generate(Player player, Weapon weapon, boolean isDefault) {
    ItemStack item = new ItemStack(weapon.item, 1);

    String name, customModelData;
    if (!isDefault) {
      CosmeticsService cosmetics = CosmeticsService.getInstance();
      String weaponModName = cosmetics.getWeaponModName(player, weapon);
      String translatableName = Optional.ofNullable(weaponModName)
          .map(s -> weapon.translatableName + "." + s)
          .orElse(weapon.translatableName);

      name = LangPlayer.of(player).getLocalized(translatableName);
      customModelData = weaponModName != null ? weaponModName : weapon.name().toLowerCase();
    } else {
      name = LangPlayer.of(player).getLocalized(weapon.translatableName);
      customModelData = weapon.name().toLowerCase();
    }

    item.editMeta(meta -> meta.displayName(Component.text(name, TextColor.color(weapon.color.asRGB()))));

    var cmd = CustomModelData.customModelData()
        .addString(customModelData)
        .build();
    item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, cmd);

    return item;
  }

  private WeaponItemGenerator() {}
}
