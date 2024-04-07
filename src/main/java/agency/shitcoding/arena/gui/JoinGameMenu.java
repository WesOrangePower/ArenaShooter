package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.localization.LangPlayer;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class JoinGameMenu {

  private final LangPlayer player;
  private final TextComponent title;

  public JoinGameMenu(Player player) {
    this.player = LangPlayer.of(player);
    this.title = Component.text(this.player.getLocalized("join.main.title"),
        TextColor.color(0xaa2222), TextDecoration.BOLD);
  }

  public void render() {
    var builder = ViewBuilder.
        builder().
        withHolder(player.getPlayer()).
        withTitle(title).
        withSize(InventorySize.TWO_ROWS).
        addItemSlot(13, ArenaMainMenu.backButton(player));

    // Add buttons for joining games
    int i = 0;
    for (Game game : GameOrchestrator.getInstance().getGames()) {
      builder.addItemSlot(i++, ArenaMainMenu.joinGameButton(player, game));
    }

    var view = builder.build();
    new ViewRenderer(view).render();
  }
}
