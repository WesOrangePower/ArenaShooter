package agency.shitcoding.arena.gui;

import static agency.shitcoding.arena.gui.ArenaControlPanels.backButton;
import static org.bukkit.persistence.PersistentDataType.STRING;

import agency.shitcoding.arena.WeaponItemGenerator;
import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.Weapon;
import net.jellycraft.guiapi.Item;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WeaponChooserMenu {

  private final LangPlayer player;
  private final Weapon weapon;

  public WeaponChooserMenu(Player player, Weapon weapon) {
    this.player = new LangPlayer(player);
    this.weapon = weapon;
  }

  public void render() {
    var builder = ViewBuilder.builder()
        .withHolder(player.getPlayer())
        .withTitle(player.getLocalized("menu.cosmetics.weaponChooser.title"))
        .withSize(InventorySize.CHEST);

    ItemStack chosenWeapon = CosmeticsService.getInstance().getWeapon(player.getPlayer(), weapon);

    var defaultWeapon = WeaponItemGenerator.$default(player.getPlayer(), weapon);
    Item defaultItem = defaultWeaponItem(defaultWeapon, defaultWeapon.equals(chosenWeapon));

    Item[] availableModdedWeapons =
        CosmeticsService.getInstance().getAvailableWeaponMods(player.getPlayer(), weapon).stream()
            .map(
                mod -> {
                  ItemStack moddedItem =
                      WeaponItemGenerator.generateMod(player.getPlayer(), weapon, mod.mod());
                  return modWeaponItem(moddedItem, mod.mod(), moddedItem.equals(chosenWeapon));
                })
            .toArray(Item[]::new);

    Item[] weapons = new Item[availableModdedWeapons.length + 1];
    weapons[0] = defaultItem;
    System.arraycopy(availableModdedWeapons, 0, weapons, 1, availableModdedWeapons.length);

    for (int i = 0; i < weapons.length; i++) {
      builder.addItemSlot(i, weapons[i]);
    }

    builder.addItemSlot(9*2+4, backButton(player, () -> new CosmeticsMenu(player.getPlayer()).render()));

    new ViewRenderer(builder.build()).render();
  }

  private Item defaultWeaponItem(ItemStack item, boolean equals) {
    var builder = ItemBuilder.builder().withItemStack(item);

    if (equals) {
      builder.withEnchantmentGlare();
      builder.withLore(Component.text(player.getLocalized("menu.cosmetics.weaponChooser.current",
          NamedTextColor.LIGHT_PURPLE)));
    }

    return builder
        .withClickAction(
            (type, ctx) -> {
              player.getPlayer().getPersistentDataContainer().remove(Keys.ofWeapon(weapon));
              CosmeticsService.getInstance().dropCache(player.getPlayer());
              new CosmeticsMenu(player.getPlayer()).render();
            })
        .build();
  }

  private Item modWeaponItem(ItemStack item, String mod, boolean equals) {
    var builder = ItemBuilder.builder().withItemStack(item);

    if (equals) {
      builder.withEnchantmentGlare();
      builder.withLore(Component.text(player.getLocalized("menu.cosmetics.weaponChooser.current",
          NamedTextColor.LIGHT_PURPLE)));
    }

    return builder
        .withClickAction(
            (type, ctx) -> {
              player
                  .getPlayer()
                  .getPersistentDataContainer()
                  .set(Keys.ofWeapon(weapon), STRING, mod);
              CosmeticsService.getInstance().dropCache(player.getPlayer());
              new CosmeticsMenu(player.getPlayer()).render();
            })
        .build();
  }
}
