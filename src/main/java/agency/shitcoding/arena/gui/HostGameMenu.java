package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamMeta;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.storage.StorageProvider;
import net.jellycraft.guiapi.Item;
import net.jellycraft.guiapi.api.ClickAction;
import net.jellycraft.guiapi.api.ItemSlot;
import net.jellycraft.guiapi.api.ViewRegistry;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import net.jellycraft.guiapi.api.views.PaginatedView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class HostGameMenu {

  private final LangPlayer player;
  Arena chosenArena;
  RuleSet chosenRuleSet;
  String chosenTeam;

  public HostGameMenu(Player player) {
    this.player = new LangPlayer(player);
  }

  public void render() {
    chooseArena();
  }

  private void chooseArena() {
    PaginatedView view = ViewBuilder.builder()
        .withHolder(player.getPlayer())
        .withTitle(player.getLocalized("menu.host.chooseArenaButton.title"))
        .build()
        .toPaginatedView()
        .withItems(getArenaItems())
        .build();

    new ViewRenderer(view).render();
  }

  private void chooseRuleSet() {
    PaginatedView view = ViewBuilder.builder()
        .withHolder(player.getPlayer())
        .withTitle(player.getLocalized("menu.host.chooseRulesetButton.title"))
        .build()
        .toPaginatedView()
        .withItems(getRuleSetItems())
        .build();

    new ViewRenderer(view).render();
  }

  private void execute() {
    ViewRegistry.closeForPlayer(player.getPlayer());
    if (chosenTeam == null) {
      player.getPlayer().performCommand(
          String.format("arena host %s %s", chosenRuleSet.name(), chosenArena.getName()));
      return;
    }
    player.getPlayer().performCommand(
        String.format("arena host %s %s %s", chosenRuleSet.name(), chosenArena.getName(),
            chosenTeam));
  }

  private List<Item> getArenaItems() {
    var usedArenaNames = GameOrchestrator.getInstance().getUsedArenaNames();
    Collection<Arena> arenas = StorageProvider.getArenaStorage().getArenas();
    return arenas.stream()
        .filter(Arena::isAllowHost)
        .filter(arena -> !usedArenaNames.contains(arena.getName()))
        .map(arena -> {
              ItemSlot build = ItemBuilder.builder()
                  .withMaterial(Material.IRON_SWORD)
                  .withName(Component.text(arena.getName(), NamedTextColor.GOLD))
                  .withLore(getArenaDescription(arena))
                  .withClickAction(arenaClickAction(arena))
                  .build();
              return new Item(build.getItemStack(), build.getOverrideClickAction());
            }
        )
        .toList();
  }

  private List<Component> getArenaDescription(Arena arena) {
    var authorPrefix = player.getLocalized("menu.host.arenaAuthors");
    var authorRepresentation = String.join(", ", arena.getAuthors());
    var authors = Component.text(authorPrefix + " " + authorRepresentation, NamedTextColor.GRAY);
    return List.of(authors);
  }

  private ClickAction arenaClickAction(Arena arena) {
    return (clickType, clickContext) -> {
      chooseRuleSet();
      this.chosenArena = arena;
    };
  }

  private List<Item> getRuleSetItems() {
    return Arrays.stream(RuleSet.values())
        .map(ruleSet -> {
              ItemSlot build = ItemBuilder.builder()
                  .withItemStack(ruleSet.getGameRules().getMenuBaseItem())
                  .withName(Component.text(player.getLocalized(ruleSet.getName()), NamedTextColor.GOLD))
                  .withClickAction(ruleSetClickAction(ruleSet))
                  .build();
              return new Item(build.getItemStack(), build.getOverrideClickAction());
            }
        )
        .toList();
  }

  private ClickAction ruleSetClickAction(RuleSet ruleSet) {
    return (clickType, clickContext) -> {
      this.chosenRuleSet = ruleSet;
      if (ruleSet.getGameRules().hasTeams()) {
        chooseTeam();
      } else {
        execute();
      }
    };
  }

  private void chooseTeam() {
    PaginatedView view = ViewBuilder.builder()
        .withHolder(player.getPlayer())
        .withTitle(player.getLocalized("menu.host.chooseTeamButton.title"))
        .build()
        .toPaginatedView()
        .withItems(getTeamItems())
        .build();

    new ViewRenderer(view).render();
  }

  private List<Item> getTeamItems() {
    return Arrays.stream(ETeam.values())
        .map(team -> {
              final TeamMeta teamMeta = team.getTeamMeta();
              ItemSlot build = ItemBuilder.builder()
                  .withMaterial(team.getIcon())
                  .withClickAction(teamClickAction(team))
                  .build();
              ItemStack itemStack = build.getItemStack();
              itemStack.editMeta(
                  meta -> meta.displayName(teamMeta.getDisplayComponent(player.getLangContext())));
              return new Item(itemStack, build.getOverrideClickAction());
            }
        )
        .toList();
  }

  private ClickAction teamClickAction(ETeam team) {
    return (clickType, clickContext) -> {
      this.chosenTeam = team.name();
      execute();
    };
  }
}
