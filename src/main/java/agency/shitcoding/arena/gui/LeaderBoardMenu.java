package agency.shitcoding.arena.gui;

import static agency.shitcoding.arena.gui.ArenaControlPanels.arrowsAndBackButton;
import static net.jellycraft.guiapi.api.fluent.ItemBuilder.itemBuilder;
import static net.jellycraft.guiapi.api.fluent.ViewBuilder.viewBuilder;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.statistics.LeaderBoardCriterion;
import agency.shitcoding.arena.statistics.Statistics;
import java.util.ArrayList;
import java.util.Optional;
import net.jellycraft.guiapi.Item;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.paginated.ControlPanelItem;
import net.jellycraft.guiapi.api.paginated.ControlPanelVisibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class LeaderBoardMenu {
  public static final LeaderBoardCriterion STANDARD_CRITERION = LeaderBoardCriterion.MATCHES;

  private final LangPlayer player;
  private final LeaderBoardCriterion criterion;

  public LeaderBoardMenu(Player player, LeaderBoardCriterion criterion) {
    this.player = new LangPlayer(player);
    this.criterion = criterion;
  }

  public LeaderBoardMenu(Player player) {
    this.player = new LangPlayer(player);
    this.criterion = STANDARD_CRITERION;
  }

  public void open() {
    var leaderboardItems = new ArrayList<Item>();
    var leaderBoard = ArenaShooter.getInstance().getStatisticsService().getLeaderboard(criterion);
    for (int i = 0; i < leaderBoard.size(); i++) {
      Item item = leaderboardToItem(i + 1, leaderBoard.poll());
      leaderboardItems.add(item);
    }

    MiniMessage mm = MiniMessage.miniMessage();
    var cp = arrowsAndBackButton(player, () -> new ArenaMainMenu(player.getPlayer()).render());

    cp.slots[8] =
        new ControlPanelItem(
            ControlPanelVisibility.ALWAYS,
            itemBuilder(Material.COMPARATOR)
                .withName(mm.deserialize(player.getLocalized("menu.lead.item.criteria")))
                .withLoreLine(mm.deserialize(player.getLocalized("menu.lead.item.criteria.lore")))
                .withClickAction((ct, ctx) -> new LeaderBoardCriterionChooserMenu(player).open())
                .build());

    var view =
        viewBuilder(player.getPlayer())
            .withTitle(
                player.getRichLocalized(
                    "menu.lead.title",
                    player.getLocalized("menu.lead.criterion." + criterion.name().toLowerCase())))
            .withSize(InventorySize.DOUBLE_CHEST)
            .build()
            .toPaginatedView()
            .withItems(leaderboardItems)
            .withControlPanel(cp)
            .build();

    new ViewRenderer(view).render();
  }

  private Item leaderboardToItem(int place, Statistics statistics) {
    var name =
        Component.text(place, NamedTextColor.GOLD)
            .append(Component.text(" - ", NamedTextColor.GRAY))
            .append(Component.text(statistics.playerName, NamedTextColor.GREEN));

    ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
    Optional.ofNullable(Bukkit.getPlayer(statistics.playerName))
        .ifPresent(p -> head.editMeta(meta -> ((SkullMeta) meta).setOwningPlayer(p)));

    return itemBuilder(Material.PLAYER_HEAD)
        .withName(name)
        .withLoreLine(
            Component.text(
                    player.getLocalized("menu.lead.criterion." + criterion.name().toLowerCase()),
                    TextColor.color(0x00ff00))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(criterion.selector.apply(statistics))))
        .build();
  }
}
