package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.gui.settings.TeamSelectGui;
import agency.shitcoding.arena.localization.LangPlayer;
import java.util.Arrays;
import java.util.Set;
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

public class ArenaMainMenu {

  public final Component title;

  private final LangPlayer player;

  public ArenaMainMenu(Player player) {
    this.player = new LangPlayer(player);
    this.title = Component.text(this.player.getLocalized("menu.main.title"),
        TextColor.color(0xaa2222), TextDecoration.BOLD);
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

    ItemSlot[] itemSlots = {hostGameOrJoinButton(), settingsButton(), statsButton(), faqButton()};

    int rowBase = 2 * 8 + 1;
    for (ItemSlot itemSlot : itemSlots) {
      rowBase += 2;
      itemSlot.setSlot(rowBase);
      viewBuilder.addItemSlot(itemSlot);
    }

    return viewBuilder.build();
  }

  private ItemSlot faqButton() {
    return ItemBuilder.builder()
        .withMaterial(Material.BOOK)
        .withName(Component.text(player.getLocalized("menu.help.title"), TextColor.color(0xa94366)))
        .withLoreLine(player.getLocalized("menu.lore.open"))
        .withLoreLine(player.getLocalized("menu.help.description"))
        .withClickAction(((clickType, clickContext) -> new FaqMenu(player.getPlayer()).render()))
        .build();
  }

  private ItemSlot statsButton() {
    return soonTmButton(player.getLocalized("menu.stat.title"));
  }

  private ItemSlot settingsButton() {
    return ItemBuilder.builder().
        withMaterial(Material.REDSTONE).
        withName(Component.text(player.getLocalized("menu.settings.title"), TextColor.color(0xa94366))).
        withLoreLine(player.getLocalized("menu.lore.open")).
        withLoreLine(player.getLocalized("menu.settings.description")).
        withClickAction(((clickType, clickContext) -> new SettingsMenu(player.getPlayer()).open())).
        build();
  }

  private ItemSlot soonTmButton(String name) {
    return ItemBuilder.builder()
        .withMaterial(Material.BARRIER)
        .withName(Component.text(name, TextColor.color(0xa94366)))
        .withLoreLine(player.getLocalized("menu.soon"))
        .build();
  }

  private ItemSlot hostGameOrJoinButton() {
    GameOrchestrator orchestrator = GameOrchestrator.getInstance();
    if (orchestrator.getGameByPlayer(player.getPlayer()).isPresent()) {
      return leaveButton();
    }

    Set<Game> games = orchestrator.getGames();
    if (games.isEmpty()) {
      return hostGameButton();
    } else {
      for (Game game : games) {
        return joinGameButton(game);
      }
    }
    throw new IllegalStateException();
  }

  private ItemSlot joinGameButton(Game game) {
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
//        Component name = switch (game.getGamestage()) {
//            case IN_PROGRESS -> Component.text("Присоединиться к начатой игре", NamedTextColor.YELLOW);
//            case WAITING -> Component.text("Присоединиться", NamedTextColor.GREEN);
//            case FINISHED -> Component.text("Закончена", NamedTextColor.DARK_PURPLE);
//        };
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
        .withClickAction((type, ctx) -> arenaJoinClickAction(game))
        .build();

    // This is needed for some reason
    item.getItemStack().editMeta(m -> m.displayName(name));

    return item;
  }

  private void arenaJoinClickAction(Game game) {
    ViewRegistry.closeForPlayer(player.getPlayer());
    if (game instanceof TeamGame teamGame) {
      new TeamSelectGui(player.getPlayer(), teamGame).open();
    } else {
      player.getPlayer().performCommand("arena join");
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
}
