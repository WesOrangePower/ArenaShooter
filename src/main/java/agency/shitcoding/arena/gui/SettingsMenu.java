package agency.shitcoding.arena.gui;

import static agency.shitcoding.arena.gui.ArenaControlPanels.backButton;
import static net.jellycraft.guiapi.api.fluent.ItemBuilder.itemBuilder;
import static net.jellycraft.guiapi.api.fluent.ViewBuilder.viewBuilder;

import agency.shitcoding.arena.localization.LangPlayer;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ItemSlot;
import net.jellycraft.guiapi.api.ViewRenderer;
import org.bukkit.entity.Player;
import su.jellycraft.jellylib.utils.HeadDatabaseUtil;

public class SettingsMenu {

  private final LangPlayer player;

  public SettingsMenu(Player player) {
    this.player = new LangPlayer(player);
  }

  public void open() {
    var view = viewBuilder(player.getPlayer())
        .withTitle(player.getLocalized("menu.settings.title"))
        .withSize(InventorySize.ONE_ROW)
        .addItemSlot(langChooser())
        .addItemSlot(8, backButton(player, () -> new ArenaMainMenu(player.getPlayer()).render()))
        .build();

    new ViewRenderer(view).render();
  }

  private ItemSlot langChooser() {
    return itemBuilder(HeadDatabaseUtil.getHead("47254"))
        .withName(player.getLocalized("menu.settings.langButton.title"))
        .withLoreLine(player.getLocalized("menu.settings.langButton.lore"))
        .withClickAction((type, ctx) -> new LangChooserMenu(player.getPlayer()).open())
        .build();
  }
}
