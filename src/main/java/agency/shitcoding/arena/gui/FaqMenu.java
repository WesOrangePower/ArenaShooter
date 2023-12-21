package agency.shitcoding.arena.gui;

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
    private final Player player;
    private final Collection<Faq> faqs;

    public FaqMenu(Player player) {
        this.player = player;
        faqs = StorageProvider.getFaqStorage().getAll();
    }

    public void render() {
        PaginatedViewBuilder builder = ViewBuilder.builder()
                .withHolder(player)
                .withTitle("Помощь")
                .withRows(3)
                .addItemSlot(backButton())
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
            Item item = new Item(build.getItemStack(), ((clickType, clickContext) -> player.openBook(faq.getBook())));
            items.add(item);
        }
        return items;
    }

    private ItemSlot backButton() {
        return ItemBuilder.builder()
                .withName(Component.text("Назад", NamedTextColor.GRAY))
                .withPersistItalics()
                .withSlot(2, 0)
                .build();
    }
}
