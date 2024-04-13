package agency.shitcoding.arena.gui.settings;

import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.GameTeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.localization.LangPlayer;
import java.util.Arrays;
import java.util.Set;
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

  private final LangPlayer player;
  private final ViewBuilder viewBuilder;
  private final TeamGame game;

  public TeamSelectGui(Player player, TeamGame game) {
    this.player = new LangPlayer(player);
    viewBuilder = ViewBuilder.builder().withHolder(player);
    this.game = game;
  }

  public void open() {
    var title = player.getLocalized("menu.host.chooseTeamButton.title");
    viewBuilder.withTitle(Component.text(title, TextColor.color(0xaa2222), TextDecoration.BOLD))
        .withSize(InventorySize.ONE_ROW);
    addItems();
    ViewRenderer viewRenderer = new ViewRenderer(viewBuilder.build());
    viewRenderer.render();
  }

  private void addItems() {
    var teams = game.getTeamManager().getTeams().entrySet();
    int slot = 0;
    for (var team : teams) {
      var players = team.getValue().getPlayers();
      viewBuilder.addItemSlot(makeItemSlot(team.getKey(), team.getValue(), players, slot++));
    }
  }

  private ItemSlot makeItemSlot(ETeam eTeam, GameTeam gameTeam, Set<Player> players, int slot) {
    var playerNames = players.stream().map(Player::getName).toArray(String[]::new);
    var builder =  ItemBuilder.builder()
        .withMaterial(eTeam.getIcon())
        .withName(gameTeam.getTeamMeta().getDisplayComponent(player.getLangContext()))
        .withLoreLine(Component.text(player.getLocalized("menu.join.chooseTeamButton.lore",
            players.size(), NamedTextColor.DARK_PURPLE)))
        .withLoreLine(Component.text(Arrays.toString(playerNames), NamedTextColor.DARK_PURPLE))
        .withLoreLine(Component.text(player.getLocalized("menu.host.chooseTeamButton.lore"),
            NamedTextColor.GRAY))
        .withSlot(slot)
        .withClickAction((type, ctx) -> {
          ViewRegistry.closeForPlayer(player.getPlayer());
          player.getPlayer().performCommand("arena join " + game.getArena().getName() + " " + eTeam.name());
        });

    return builder.build();
  }


}
