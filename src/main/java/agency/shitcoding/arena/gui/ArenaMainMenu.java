package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.gui.settings.TeamSelectGui;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.statistics.Statistics;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ItemSlot;
import net.jellycraft.guiapi.api.ViewRegistry;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import net.jellycraft.guiapi.api.views.View;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ArenaMainMenu {

  public final Component title;

  private final LangPlayer player;

  public ArenaMainMenu(Player player) {
    this.player = new LangPlayer(player);
    this.title = Component.text(this.player.getLocalized("menu.main.title"),
        TextColor.color(0xaa2222), TextDecoration.BOLD);
  }

  public static ItemSlot backButton(LangPlayer player, Runnable action) {
    return ItemBuilder.builder()
        .withMaterial(Material.ARROW)
        .withName(Component.text(player.getLocalized("menu.backButton.name"),
            TextColor.color(0xbb2222)))
        .withLoreLine(Component.text(player.getLocalized("menu.backButton.description"),
            TextColor.color(0x882222)))
        .withClickAction((type, ctx) -> action.run())
        .build();
  }

  public void render() {
    View view = getView();
    ViewRenderer viewRenderer = new ViewRenderer(view);
    viewRenderer.render();
  }

  private View getView() {
    ViewBuilder viewBuilder = ViewBuilder.builder()
        .withTitle(title)
        .withHolder(player.getPlayer())
        .withSize(InventorySize.DOUBLE_CHEST_NO_BOTTOM_ROW);

    ItemSlot[] itemSlots = getGameButtons();
    int row = 2;
    for (ItemSlot itemSlot : itemSlots) {
      itemSlot.setSlot(row++ * 9 + 1);
      viewBuilder.addItemSlot(itemSlot);
    }

    viewBuilder.addItemSlot(settingsButton());
    viewBuilder.addItemSlot(statsButton());
    viewBuilder.addItemSlot(cosmeticsButton());
//    viewBuilder.addItemSlot(faqButton());

    if (player.getPlayer().hasPermission(ArenaDeathMatchCommand.ADMIN_PERM) && isJellyRestartLoaded())
      viewBuilder.addItemSlot(restartServerButton());

    return viewBuilder.build();
  }

  private ItemSlot cosmeticsButton() {
    return ItemBuilder.builder()
        .withMaterial(Material.CHEST)
        .withName(Component.text(player.getLocalized("menu.cosmetics.title"), TextColor.color(0xa94366)))
        .withLoreLine(player.getLocalized("menu.lore.open"))
        .withLoreLine(player.getLocalized("menu.cosmetics.description"))
        .withClickAction((clickType, clickContext) -> new CosmeticsMenu(player.getPlayer()).render())
        .withSlot(2, 7)
        .build();
  }


  private ItemSlot faqButton() {
    return ItemBuilder.builder()
        .withMaterial(Material.BOOK)
        .withName(Component.text(player.getLocalized("menu.help.title"), TextColor.color(0xa94366)))
        .withLoreLine(player.getLocalized("menu.lore.open"))
        .withLoreLine(player.getLocalized("menu.help.description"))
        .withClickAction((clickType, clickContext) -> new FaqMenu(player.getPlayer()).render())
        .withSlot(2, 7)
        .build();
  }

  private ItemSlot statsButton() {
    var itemBuilder = ItemBuilder.builder()
        .withMaterial(Material.ENCHANTED_BOOK)
        .withName(Component.text(
            player.getLocalized("menu.stat.title"), TextColor.color(0xa94366)
        ))
        .withLore(getStatsLore())
        .withSlot(2, 5)
        .withClickAction(((clickType, clickContext) -> new StatsMenu(player.getPlayer()).open()));

    return itemBuilder.build();
  }

  private Component[] getStatsLore() {
    Statistics statistics = ArenaShooter.getInstance()
        .getStatisticsService()
        .getStatistics(player.getPlayer());

    MiniMessage mm = MiniMessage.miniMessage();

    var totalKills = player.getLocalized("menu.stat.totalKills", statistics.totalKills);
    var totalGames = player.getLocalized("menu.stat.totalGames", statistics.totalGames);
    var matchesWon = player.getLocalized("menu.stat.matchesWon", statistics.matchesWon);
    var killDeathRatio = player.getLocalized("menu.stat.killDeathRatio", statistics.killDeathRatio);

    var open = player.getLocalized("menu.lore.open");

    return new Component[]{
        mm.deserialize(totalKills),
        mm.deserialize(totalGames),
        mm.deserialize(matchesWon),
        mm.deserialize(killDeathRatio),
        Component.text(""),
        mm.deserialize("<gray><i>" + open)
    };

  }

  private ItemSlot settingsButton() {
    return ItemBuilder.builder().
        withMaterial(Material.REDSTONE).
        withName(
            Component.text(player.getLocalized("menu.settings.title"), TextColor.color(0xa94366))).
        withLoreLine(player.getLocalized("menu.lore.open")).
        withLoreLine(player.getLocalized("menu.settings.description")).
        withClickAction(((clickType, clickContext) -> new SettingsMenu(player.getPlayer()).open())).
        withSlot(2, 3).
        build();
  }

  private ItemSlot[] getGameButtons() {
    GameOrchestrator orchestrator = GameOrchestrator.getInstance();
    if (orchestrator.getGameByPlayer(player.getPlayer()).isPresent()) {
      return new ItemSlot[] {leaveButton()};
    }

    Game[] games = orchestrator.getGames().toArray(Game[]::new);
    var count = games.length;

    if (count == 0) {
      return new ItemSlot[] {hostGameButton()};
    }
    if (count == 1) {
      return new ItemSlot[] {hostGameButton(), joinGameButton(player, games[0])};
    } else {
      return new ItemSlot[] {hostGameButton(), selectJoinGameMenuButton()};
    }
  }

  private ItemSlot selectJoinGameMenuButton() {
    var count = Math.min(GameOrchestrator.getInstance().getGames().size(), 64);

    return ItemBuilder.builder()
        .withMaterial(Material.TARGET)
        .withAmount(count)
        .withName(Component.text(player.getLocalized("menu.selectButton.title"),
            TextColor.color(0x22bb22)))
        .withLoreLine(Component.text(player.getLocalized("menu.selectButton.description")))
        .withClickAction((type, ctx) -> new JoinGameMenu(player.getPlayer()).render())
        .build();
  }

  public static ItemSlot joinGameButton(LangPlayer player, Game game) {
    var rulesetName = player.getLocalized(game.getRuleSet().getName());
    var arenaName = game.getArena().getName();
    Component name = MiniMessage.miniMessage().deserialize(
        player.getLocalized("menu.joinButton.title", rulesetName, arenaName)
    );

    Material material = switch (game.getGamestage()) {
      case IN_PROGRESS -> Material.YELLOW_WOOL;
      case WAITING -> Material.GREEN_WOOL;
      case FINISHED -> Material.PURPLE_WOOL;
    };
    String[] playerNames = game.getPlayers().stream()
        .map(Player::getName)
        .toArray(String[]::new);

    ItemSlot item = ItemBuilder.builder()
        .withName(name)
        .withMaterial(material)
        .withLoreLine(Component.text(player.getLocalized("menu.joinButton.playerCount",
            game.getPlayers().size(), game.getRuleSet().getMaxPlayers()
        )))
        .withLoreLine(Component.text(Arrays.toString(playerNames)))
        .withClickAction((type, ctx) -> arenaJoinClickAction(player.getPlayer(), game))
        .build();

    // This is needed for some reason
    item.getItemStack().editMeta(m -> m.displayName(name));

    return item;
  }

  public static void arenaJoinClickAction(Player player, Game game) {
    ViewRegistry.closeForPlayer(player);
    if (game instanceof TeamGame teamGame) {
      new TeamSelectGui(player, teamGame).open();
    } else {
      player.performCommand("arena join " + game.getArena().getName());
    }
  }

  private ItemSlot hostGameButton() {
    return ItemBuilder.builder()
        .withMaterial(Material.DIAMOND_SWORD)
        .withName(Component.text(player.getLocalized("menu.hostButton.name"),
            TextColor.color(0x22bb22)))
        .withLore(Component.text(player.getLocalized("menu.hostButton.description"),
            TextColor.color(0x228822)))
        .withClickAction(
            ((clickType, clickContext) -> new HostGameMenu(player.getPlayer()).render()))
        .build();
  }

  private ItemSlot leaveButton() {
    return ItemBuilder.builder()
        .withMaterial(Material.ARROW)
        .withName(Component.text(player.getLocalized("menu.leaveButton.name"),
            TextColor.color(0xbb2222)))
        .withLore(Component.text(player.getLocalized("menu.leaveButton.description"),
            TextColor.color(0x882222)))
        .withClickAction(
            ((clickType, clickContext) -> player.getPlayer().performCommand("arena leave")))
        .build();
  }

  private ItemSlot restartServerButton() {
    return ItemBuilder.builder()
        .withMaterial(Material.REDSTONE_BLOCK)
        .withName(Component.text(player.getLocalized("menu.restartServerButton.name"),
            TextColor.color(0xbb2222)))
        .withLore(Component.text(player.getLocalized("menu.restartServerButton.description"),
            TextColor.color(0x882222)))
        .withClickAction(
            ((clickType, clickContext) -> player.getPlayer().performCommand("jellyrestart:restart start 5")))
        .withSlot(4, 8)
        .build();
  }

  private static boolean isJellyRestartLoaded() {
    return ArenaShooter.getInstance().getServer().getPluginManager().isPluginEnabled("JellyRestart");
  }

}
