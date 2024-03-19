package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.localization.LangPlayer;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ItemSlot;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import org.bukkit.entity.Player;
import su.jellycraft.jellylib.utils.HeadDatabaseUtil;

public class SettingsMenu {

  private final LangPlayer player;

  public SettingsMenu(Player player) {
    this.player = new LangPlayer(player);
  }

  public void open() {
    var view = new ViewBuilder()
        .withTitle(player.getLocalized("menu.settings.title"))
        .withSize(InventorySize.ONE_ROW)
        .withHolder(player.getPlayer())
        .addItemSlot(langChooser())
        .build();

    new ViewRenderer(view).render();
  }

  private ItemSlot langChooser() {
    return ItemBuilder.builder()
        .withItemStack(HeadDatabaseUtil.getHead("47254"))
        .withName(player.getLocalized("menu.settings.langButton.title"))
        .withLoreLine(player.getLocalized("menu.settings.langButton.lore"))
        .withClickAction((type, ctx) -> {
          new LangChooserMenu(player.getPlayer()).open();
        })
        .build();
  }
}
