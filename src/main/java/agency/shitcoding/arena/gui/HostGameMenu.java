package agency.shitcoding.arena.gui;

import static agency.shitcoding.arena.gui.ArenaControlPanels.backButton;
import static net.jellycraft.guiapi.api.fluent.ItemBuilder.itemBuilder;
import static net.jellycraft.guiapi.api.fluent.ViewBuilder.viewBuilder;

import agency.shitcoding.arena.gamestate.GameRuleSerializer;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamMeta;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.GameRules;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.storage.StorageProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.jellycraft.guiapi.Item;
import net.jellycraft.guiapi.api.ClickAction;
import net.jellycraft.guiapi.api.ItemSlot;
import net.jellycraft.guiapi.api.ViewRegistry;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.paginated.ControlPanel;
import net.jellycraft.guiapi.api.paginated.ControlPanelItem;
import net.jellycraft.guiapi.api.paginated.ControlPanelVisibility;
import net.jellycraft.guiapi.api.views.PaginatedView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HostGameMenu {

  private final LangPlayer player;
  private Arena chosenArena;
  private RuleSet chosenRuleSet;
  private String chosenTeam;
  private GameRules chosenGameRules;

  public HostGameMenu(Player player) {
    this.player = new LangPlayer(player);
  }

  public void render() {
    chooseArena();
  }

  private void chooseArena() {

    PaginatedView view =
        viewBuilder(player.getPlayer())
            .withTitle(player.getLocalized("menu.host.chooseArenaButton.title"))
            .build()
            .toPaginatedView()
            .withItems(getArenaItems())
            .withControlPanel(backControlPanel())
            .build();

    new ViewRenderer(view).render();
  }

  private void chooseRuleSet() {
    PaginatedView view =
        viewBuilder(player.getPlayer())
            .withTitle(player.getLocalized("menu.host.chooseRulesetButton.title"))
            .build()
            .toPaginatedView()
            .withItems(getRuleSetItems())
            .withControlPanel(backControlPanel())
            .build();

    new ViewRenderer(view).render();
  }

  private void execute() {
    ViewRegistry.closeForPlayer(player.getPlayer());
    StringBuilder sb =
        new StringBuilder("arena host ")
            .append(chosenRuleSet.name())
            .append(" ")
            .append(chosenArena.getName());
    if (chosenTeam != null) {
      sb.append(" ");
      sb.append(chosenTeam);
    }

    if (chosenGameRules != null) {
      sb.append(" ");
      sb.append(new GameRuleSerializer().serialize(chosenGameRules));
    }

    player.getPlayer().performCommand(sb.toString());
  }

  private List<Item> getArenaItems() {
    Collection<Arena> arenas = StorageProvider.getArenaStorage().getArenas();
    return arenas.stream()
        .filter(Arena::isAllowHost)
        .filter(arena -> !arena.getSupportedRuleSets().isEmpty())
        .map(
            arena -> {
              ItemSlot build =
                  itemBuilder(Material.IRON_SWORD)
                      .withName(Component.text(arena.getName(), NamedTextColor.GOLD))
                      .withLore(getArenaDescription(arena))
                      .withClickAction(arenaClickAction(arena))
                      .build();
              return new Item(build.getItemStack(), build.getOverrideClickAction());
            })
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
      this.chosenArena = arena;
      chooseRuleSet();
    };
  }

  private List<Item> getRuleSetItems() {
    return Arrays.stream(RuleSet.values())
        .filter(ruleSet -> chosenArena.getSupportedRuleSets().contains(ruleSet))
        .map(
            ruleSet -> {
              ItemSlot build =
                  itemBuilder(ruleSet.getMenuBaseItem())
                      .withName(
                          Component.text(
                              player.getLocalized(ruleSet.getName()), NamedTextColor.GOLD))
                      .withClickAction(ruleSetClickAction(ruleSet))
                      .withLore(getGameRulesDescription(ruleSet))
                      .build();
              return new Item(build.getItemStack(), build.getOverrideClickAction());
            })
        .toList();
  }

  private List<Component> getGameRulesDescription(RuleSet ruleSet) {
    var gameRules = ruleSet.getDefaultGameRules();

    long gameLengthSeconds = gameRules.gameLengthSeconds();
    long minutes = (gameLengthSeconds % 3600) / 60;
    long seconds = gameLengthSeconds % 60;

    String timeString = String.format("%02d:%02d", minutes, seconds);
    var list = new ArrayList<Component>();
    list.add(player.getRichLocalized("menu.host.gameRules.gameLengthSeconds", timeString));
    list.add(
        player.getRichLocalized(
            "menu.host.gameRules.players", gameRules.minPlayers(), gameRules.maxPlayers()));
    if (gameRules.dropMostValuableWeaponOnDeath()) {
      list.add(player.getRichLocalized("menu.host.gameRules.dropMostValuableWeaponOnDeath"));
    }
    if (gameRules.fastWeaponSpawn()) {
      list.add(player.getRichLocalized("menu.host.gameRules.fastWeaponSpawn"));
    }

    list.add(Component.empty());
    list.add(player.getRichLocalized("menu.host.gameRules.shiftForOptions"));

    return list;
  }

  private ClickAction ruleSetClickAction(RuleSet ruleSet) {
    return (clickType, clickContext) -> {
      this.chosenRuleSet = ruleSet;
      if (this.chosenGameRules == null) {
        this.chosenGameRules = ruleSet.getDefaultGameRules();
      }
      if (clickType.isShiftClick()) {
        new CustomGameRulesDialog(
                player,
                chosenGameRules,
                this::chooseRuleSet,
                newGameRules -> {
                  this.chosenGameRules = newGameRules;
                  if (chosenRuleSet.isTeamBased()) {
                    chooseTeam();
                  } else {
                    execute();
                  }
                })
            .render();
        return;
      }
      if (chosenRuleSet.isTeamBased()) {
        chooseTeam();
      } else {
        execute();
      }
    };
  }

  private void chooseTeam() {
    PaginatedView view =
        viewBuilder(player.getPlayer())
            .withTitle(player.getLocalized("menu.host.chooseTeamButton.title"))
            .build()
            .toPaginatedView()
            .withItems(getTeamItems())
            .build();

    new ViewRenderer(view).render();
  }

  private List<Item> getTeamItems() {
    return Arrays.stream(ETeam.values())
        .map(
            team -> {
              final TeamMeta teamMeta = team.getTeamMeta();
              ItemSlot build =
                  itemBuilder(team.getIcon()).withClickAction(teamClickAction(team)).build();
              ItemStack itemStack = build.getItemStack();
              itemStack.editMeta(
                  meta -> meta.displayName(teamMeta.getDisplayComponent(player.getLangContext())));
              return new Item(itemStack, build.getOverrideClickAction());
            })
        .toList();
  }

  private ClickAction teamClickAction(ETeam team) {
    return (clickType, clickContext) -> {
      this.chosenTeam = team.name();
      execute();
    };
  }

  private ControlPanel backControlPanel() {
    ItemSlot backButton = backButton(player, () -> new ArenaMainMenu(player.getPlayer()).render());
    backButton.setSlot(8);
    return new ControlPanel(new ControlPanelItem(ControlPanelVisibility.ALWAYS, backButton))
        .doNotRequireExtraRow();
  }
}
