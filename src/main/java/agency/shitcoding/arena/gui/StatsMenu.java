package agency.shitcoding.arena.gui;

import static agency.shitcoding.arena.gui.ArenaMainMenu.backButton;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.statistics.GameOutcome;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Locale;
import net.jellycraft.guiapi.Item;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import net.jellycraft.guiapi.api.paginated.ControlPanelItem;
import net.jellycraft.guiapi.api.paginated.ControlPanelVisibility;
import net.jellycraft.guiapi.api.paginated.ControlPanels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class StatsMenu {
  private final LangPlayer player;

  public StatsMenu(Player player) {
    this.player = new LangPlayer(player);
  }

  public void open() {
    var gameOutcomes = ArenaShooter.getInstance().getStatisticsService()
        .getGameOutcomes(player.getPlayer())
        .stream()
        .sorted(Comparator.comparing(GameOutcome::time).reversed())
        .map(this::gameOutcomeToItem)
        .toList();

    if (gameOutcomes.isEmpty()) {
      return;
    }

    MiniMessage mm = MiniMessage.miniMessage();
    var cp = ControlPanels.namedArrows(
        mm.deserialize("<aqua>" + player.getLocalized("menu.previousArrow")),
        mm.deserialize("<aqua>" + player.getLocalized("menu.nextArrow"))
    );

    cp.slots[4] = new ControlPanelItem(
        ControlPanelVisibility.ALWAYS,
        backButton(player, () -> new ArenaMainMenu(player.getPlayer()).render())
    );


    var view = ViewBuilder.builder()
            .withTitle(Component.text(player.getLocalized("menu.stat.title")))
            .withSize(InventorySize.DOUBLE_CHEST)
            .withHolder(player.getPlayer())
            .build()
            .toPaginatedView()
            .withItems(gameOutcomes)
            .withControlPanel(cp)
            .build();

    new ViewRenderer(view).render();
  }

  private Item gameOutcomeToItem(GameOutcome gameOutcome) {
    MiniMessage mm = MiniMessage.miniMessage();

    var ruleset = player.getLocalized(gameOutcome.ruleSet().getName());
    var victory = player.getLocalized(gameOutcome.isWon()
        ? "menu.stat.item.win"
        : "menu.stat.item.loss"
    );
    var name = player.getLocalized("menu.stat.item.title", ruleset, gameOutcome.map(), victory);

    var locale = new Locale(player.getLangContext().getLocale());
    var formatted = gameOutcome.time().atZone(ZoneId.of("UTC"))
        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(locale));
    var playedAt = player.getLocalized("menu.stat.item.playedAt", formatted);
    var kills = player.getLocalized("menu.stat.item.kills", gameOutcome.kills());
    var kdr = gameOutcome.deaths() == 0
        ? player.getLocalized("menu.stat.item.killDeathRatio", gameOutcome.kills())
        : player.getLocalized("menu.stat.item.killDeathRatio", (float)gameOutcome.kills() / gameOutcome.deaths());

    return ItemBuilder.builder()
        .withMaterial(gameOutcome.isWon() ? Material.IRON_SWORD : Material.STONE_SWORD)
        .withName(mm.deserialize(name))
        .withLoreLine(mm.deserialize(playedAt))
        .withLoreLine(mm.deserialize(kills))
        .withLoreLine(mm.deserialize(kdr))
        .build();
  }
}
