package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.GameTeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ItemSlot;
import net.jellycraft.guiapi.api.ViewRegistry;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class TeamSelectGui {
  private final Player player;
  private final ViewBuilder viewBuilder;
  private final TeamGame game;

  public TeamSelectGui(Player player, TeamGame game) {
    this.player = player;
    viewBuilder = ViewBuilder.builder().withHolder(player);
    this.game = game;
  }

  public void open() {
    viewBuilder.withTitle(Component.text("Выбор команды",
            TextColor.color(0xaa2222), TextDecoration.BOLD))
        .withSize(InventorySize.ONE_ROW);
    addItems();
    ViewRenderer viewRenderer = new ViewRenderer(viewBuilder.build());
    viewRenderer.render();
  }

  private void addItems() {
    var teams = game.getTeamManager().getTeams().entrySet();
    int slot = 0;
    for (var team: teams) {
      viewBuilder.addItemSlot(makeItemSlot(team.getKey(), team.getValue(), slot++));
    }
  }

  private ItemSlot makeItemSlot(ETeam eTeam, GameTeam gameTeam, int slot) {
    return ItemBuilder.builder()
        .withMaterial(eTeam.getIcon())
        .withName(gameTeam.getTeamMeta().getDisplayComponent())
        .withLoreLine(Component.text("Нажмите, чтобы присоединиться", NamedTextColor.GRAY))
        .withSlot(slot)
        .withClickAction((type, ctx) -> {
          ViewRegistry.closeForPlayer(player);
          player.performCommand("arena join " + eTeam.name());
        })
        .build();
  }


}
