package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.localization.LangPlayer;
import net.jellycraft.guiapi.api.ItemSlot;
import net.jellycraft.guiapi.api.clickactions.BackwardClickAction;
import net.jellycraft.guiapi.api.clickactions.ForwardClickAction;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.paginated.ControlPanel;
import net.jellycraft.guiapi.api.paginated.ControlPanelItem;
import net.jellycraft.guiapi.api.paginated.ControlPanelVisibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;

import static agency.shitcoding.arena.gui.GuiUtils.addModelData;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public interface ArenaControlPanels {
  static ControlPanelItem prevArrowNamed(Component prevName) {
    return new ControlPanelItem(
        ControlPanelVisibility.NOT_FIRST_PAGE,
        addModelData(
            "previous",
            ItemBuilder.builder()
                .withMaterial(Material.ARROW)
                .withName(prevName)
                .withClickAction(new BackwardClickAction())
                .withSlot(2)
                .build()));
  }

  static ControlPanelItem nextArrowNamed(Component nextName) {
    return new ControlPanelItem(
        ControlPanelVisibility.NOT_LAST_PAGE,
        addModelData(
            "next",
            ItemBuilder.builder()
                .withMaterial(Material.ARROW)
                .withName(nextName)
                .withClickAction(new ForwardClickAction())
                .withSlot(6)
                .build()));
  }

  static ControlPanel namedArrows(Component prevName, Component nextName) {
    return new ControlPanel(prevArrowNamed(prevName), nextArrowNamed(nextName));
  }

  static ControlPanel namedArrows(LangPlayer player) {
    MiniMessage mm = miniMessage();
    return namedArrows(
        mm.deserialize("<aqua>" + player.getLocalized("menu.previousArrow")),
        mm.deserialize("<aqua>" + player.getLocalized("menu.nextArrow")));
  }

  static ControlPanel arrowsAndBackButton(LangPlayer player, Runnable backAction) {
    var cp = namedArrows(player);
    cp.slots[4] =
        new ControlPanelItem(ControlPanelVisibility.ALWAYS, backButton(player, backAction));
    return cp;
  }

  static ItemSlot backButton(LangPlayer player, Runnable action) {
    return addModelData(
        "back",
        ItemBuilder.builder()
            .withMaterial(Material.ARROW)
            .withName(
                Component.text(
                    player.getLocalized("menu.backButton.name"), TextColor.color(0xbb2222)))
            .withLoreLine(
                Component.text(
                    player.getLocalized("menu.backButton.description"), TextColor.color(0x882222)))
            .withClickAction((type, ctx) -> action.run())
            .build());
  }
}
