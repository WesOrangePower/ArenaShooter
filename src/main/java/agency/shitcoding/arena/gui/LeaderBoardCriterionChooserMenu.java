package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.statistics.LeaderBoardCriterion;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ItemSlot;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardCriterionChooserMenu {
  private final LangPlayer player;

  public LeaderBoardCriterionChooserMenu(LangPlayer player) {
    this.player = player;
  }

  private List<ItemSlot> criterionButtons() {
    LeaderBoardCriterion[] criteria = LeaderBoardCriterion.values();

    List<ItemSlot> slots = new ArrayList<>();
    for (int i = 0, criteriaLength = criteria.length; i < criteriaLength; i++) {
      LeaderBoardCriterion criterion = criteria[i];
      var item = ItemBuilder.builder()
          .withMaterial(Material.COMPASS)
          .withName(player.getRichLocalized("menu.lead.criterion." + criterion.name().toLowerCase()))
          .withLore(player.getRichLocalized("menu.lead.criteria.choose.lore"))
          .withSlot(i)
          .withClickAction((ct, ctx) -> new LeaderBoardMenu(player.getPlayer(), criterion).open())
          .build();
      slots.add(item);
    }

    return slots;
  }

  public void open() {
    var builder = ViewBuilder.builder()
        .withTitle(player.getRichLocalized("menu.lead.criteria.choose.title"))
        .withSize(InventorySize.ONE_ROW)
        .withHolder(player.getPlayer());

    addItems(builder);

    builder.build().open();
  }

  private void addItems(ViewBuilder builder) {
    criterionButtons().forEach(builder::addItemSlot);
  }
}
