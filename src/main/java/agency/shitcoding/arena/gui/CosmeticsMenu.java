package agency.shitcoding.arena.gui;

import static agency.shitcoding.arena.gui.ArenaMainMenu.backButton;

import agency.shitcoding.arena.WeaponItemGenerator;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Weapon;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import org.bukkit.entity.Player;

public class CosmeticsMenu {

  private final LangPlayer player;

  public CosmeticsMenu(Player player) {
    this.player = new LangPlayer(player);
  }

  public void render() {
    var builder = ViewBuilder.builder()
        .withHolder(player.getPlayer())
        .withTitle(player.getLocalized("menu.cosmetics.title"))
        .withSize(InventorySize.TWO_ROWS)
        .addItemSlot(13, backButton(player, () -> new ArenaMainMenu(player.getPlayer()).render()));

    var weapons = Weapon.values();
    for(int i = 0; i < weapons.length; i++) {
      Weapon weapon = weapons[i];
      var defaultWeapon = WeaponItemGenerator.$default(player.getPlayer(), weapon);
      builder.addItemSlot(i, defaultWeapon, (clickType, clickContext) ->
          new WeaponChooserMenu(player.getPlayer(), weapon).render()
      );
    }

    new ViewRenderer(builder.build()).render();
  }
}
