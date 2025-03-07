package agency.shitcoding.arena.gui;

import static agency.shitcoding.arena.gui.ArenaMainMenu.backButton;

import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Faq;
import agency.shitcoding.arena.storage.StorageProvider;
import net.jellycraft.guiapi.Item;
import net.jellycraft.guiapi.api.ItemSlot;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.fluent.PaginatedViewBuilder;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import net.jellycraft.guiapi.api.views.PaginatedView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FaqMenu {

  private final LangPlayer player;
  private final Collection<Faq> faqs;

  public FaqMenu(Player player) {
    this.player = new LangPlayer(player);
    faqs = StorageProvider.getFaqStorage().getAll();
  }

  public void render() {
    PaginatedViewBuilder builder = ViewBuilder.builder()
        .withHolder(player.getPlayer())
        .withTitle(player.getLocalized("menu.help.title"))
        .withRows(3)
        .addItemSlot(9*2, backButton(player, () -> new ArenaMainMenu(player.getPlayer()).render()))
        .build()
        .toPaginatedView();

    PaginatedView view = builder
        .withItems(getBookItems())
        .build();

    new ViewRenderer(view).render();
  }

  private List<Item> getBookItems() {
    ArrayList<Item> items = new ArrayList<>();
    for (Faq faq : faqs) {
      ItemSlot build = ItemBuilder.builder()
          .withMaterial(Material.WRITTEN_BOOK)
          .withName(Component.text(faq.getTitle(), NamedTextColor.GOLD))
          .build();
      Item item = new Item(build.getItemStack(),
          ((clickType, clickContext) -> player.getPlayer().openBook(faq.getBook())));
      items.add(item);
    }
    return items;
  }
}
