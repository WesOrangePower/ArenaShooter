package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.gamestate.Lobby;
import agency.shitcoding.arena.localization.LangPlayer;
import net.jellycraft.guiapi.Item;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ViewRegistry;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import org.bukkit.entity.Player;
import su.jellycraft.jellylib.utils.HeadDatabaseUtil;

public class LangChooserMenu {

  private final LangPlayer langPlayer;

  public LangChooserMenu(Player player) {
    langPlayer = new LangPlayer(player);
  }

  public void open() {
    var builder = new ViewBuilder()
        .withTitle(langPlayer.getLocalized("menu.settings.langButton.title"))
        .withSize(InventorySize.ONE_ROW)
        .withHolder(langPlayer.getPlayer());

    var langToHead = mapLocaleToHead();
    for (int i = 0; i < langToHead.length; i++) {
      builder.addItemSlot(i, getItem(langToHead[i][0], langToHead[i][1]));
    }

    new ViewRenderer(builder.build()).render();
  }

  private Item getItem(String lang, String head) {
    var langName = langPlayer.getLocalized("menu.settings.lang." + lang);
    return ItemBuilder.builder().withItemStack(HeadDatabaseUtil.getHead(head))
        .withName(langName)
        .withLoreLine(langPlayer.getLocalized("menu.settings.lang." + lang + ".lore"))
        .withClickAction((type, ctx) -> {
          langPlayer.setLocale(lang);
          langPlayer.sendRichLocalized("menu.settings.lang.langChanged", langName);
          ViewRegistry.closeForPlayer(langPlayer.getPlayer());
          Lobby.getInstance().sendPlayer(langPlayer.getPlayer());
        }).build();
  }

  private String[][] mapLocaleToHead() {
    return new String[][] {
        new String[] {"en", "890"},
        new String[] {"et", "4399"},
        new String[] {"ru", "9299"},
        new String[] {"uk", "5578"},
        new String[] {"pt", "27585"},
    };
  }
}
