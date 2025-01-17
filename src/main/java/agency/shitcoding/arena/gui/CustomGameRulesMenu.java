package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.CustomGameRulesBuilder;
import agency.shitcoding.arena.models.GameRules;
import java.util.List;
import java.util.function.Consumer;
import net.jellycraft.guiapi.Item;
import net.jellycraft.guiapi.api.ItemSlot;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import org.bukkit.Material;

public class CustomGameRulesMenu {
  private final LangPlayer langPlayer;
  private final Runnable onDiscard;
  private final Consumer<GameRules> onGameRulesChanged;
  private final CustomGameRulesBuilder customGameRulesBuilder;

  /**
   * Constructor.
   *
   * @param langPlayer player to whom the menu is displayed
   * @param baseGameRules current game rules.
   * @param onGameRulesChanged callback to be called when the player confirms the changes. Closing
   *     the inventory should happen on the caller side.
   */
  public CustomGameRulesMenu(
      LangPlayer langPlayer,
      GameRules baseGameRules,
      Runnable onDiscard,
      Consumer<GameRules> onGameRulesChanged) {
    this.langPlayer = langPlayer;
    this.onDiscard = onDiscard;
    this.onGameRulesChanged = onGameRulesChanged;
    this.customGameRulesBuilder = CustomGameRulesBuilder.basedOn(baseGameRules);
  }

  public void render() {
    final int rows = 2;
    var builder =
        ViewBuilder.builder()
            .withHolder(langPlayer.getPlayer())
            .withTitle(langPlayer.getRichLocalized("menu.customRules.title"))
            .withRows(rows);

    addButtons(builder);

    builder.addItemSlot(confirmButton(9 * rows - 1));
    builder.addItemSlot(discardButton(9 * rows - 2));

    builder.build().open();
  }

  private void addButtons(ViewBuilder builder) {
    var buttons = getButtons();

    for (int i = 0; i < buttons.size(); i++) {
      builder.addItemSlot(i, buttons.get(i));
    }
  }

  private List<Item> getButtons() {
    return List.of(
        ItemBuilder.builder()
            .withMaterial(Material.CLOCK)
            .withLore(
                langPlayer.getRichLocalized(
                    "menu.customRules.value", customGameRulesBuilder.getGameLengthSeconds() / 60))
            .withName(langPlayer.getRichLocalized("menu.customRules.gameLengthSeconds"))
            .withClickAction(
                new TextInputClickAction(
                    langPlayer.getLocalized("menu.customRules.gameLengthSeconds.prompt"),
                    input -> {
                      try {
                        customGameRulesBuilder.setGameLengthSeconds(validNaturalLong(input) * 60);
                      } catch (NumberFormatException e) {
                        langPlayer.sendRichLocalized("menu.customRules.invalidNumber");
                      }
                      render();
                    }))
            .build(),
        ItemBuilder.builder()
            .withMaterial(Material.BLUE_CARPET)
            .withLore(
                langPlayer.getRichLocalized(
                    "menu.customRules.value", customGameRulesBuilder.getMinPlayers()))
            .withName(langPlayer.getRichLocalized("menu.customRules.minPlayers"))
            .withClickAction(
                new TextInputClickAction(
                    langPlayer.getLocalized("menu.customRules.minPlayers.prompt"),
                    input -> {
                      try {
                        customGameRulesBuilder.setMinPlayers(validNaturalInt(input));
                      } catch (NumberFormatException e) {
                        langPlayer.sendRichLocalized("menu.customRules.invalidNumber");
                      }
                      render();
                    }))
            .build(),
        ItemBuilder.builder()
            .withMaterial(Material.RED_CARPET)
            .withLore(
                langPlayer.getRichLocalized(
                    "menu.customRules.value", customGameRulesBuilder.getMaxPlayers()))
            .withName(langPlayer.getRichLocalized("menu.customRules.maxPlayers"))
            .withClickAction(
                new TextInputClickAction(
                    langPlayer.getLocalized("menu.customRules.maxPlayers.prompt"),
                    input -> {
                      try {
                        customGameRulesBuilder.setMaxPlayers(validNaturalInt(input));
                      } catch (NumberFormatException e) {
                        langPlayer.sendRichLocalized("menu.customRules.invalidNumber");
                      }
                      render();
                    }))
            .build(),
        ItemBuilder.builder()
            .withMaterial(Boolean.TRUE.equals(customGameRulesBuilder.getDropMostValuableWeaponOnDeath())
                ? Material.CHEST
                : Material.ENDER_CHEST)
            .withLore(
                langPlayer.getRichLocalized(
                    "menu.customRules.value",
                    customGameRulesBuilder.getDropMostValuableWeaponOnDeath()))
            .withName(langPlayer.getLocalized("menu.customRules.dropMostValuableWeaponOnDeath"))
            .withClickAction(
                (ct, ctx) -> {
                  customGameRulesBuilder.setDropMostValuableWeaponOnDeath(
                      !customGameRulesBuilder.getDropMostValuableWeaponOnDeath());
                  render();
                })
            .build());
  }

  private ItemSlot discardButton(@SuppressWarnings("SameParameterValue") int slot) {
    return ItemBuilder.builder()
        .withMaterial(Material.RED_STAINED_GLASS_PANE)
        .withName(langPlayer.getRichLocalized("menu.customRules.discard"))
        .withClickAction((player, event) -> onDiscard.run())
        .withSlot(slot)
        .build();
  }

  private ItemSlot confirmButton(@SuppressWarnings("SameParameterValue") int slot) {
    return ItemBuilder.builder()
        .withMaterial(Material.LIME_STAINED_GLASS_PANE)
        .withName(langPlayer.getRichLocalized("menu.customRules.confirm"))
        .withClickAction(
            (player, event) -> onGameRulesChanged.accept(customGameRulesBuilder.build()))
        .withSlot(slot)
        .build();
  }

  private long validNaturalLong(String l) {
    long parsed = Long.parseLong(l);
    if (parsed < 0) {
      throw new NumberFormatException("Negative number");
    }
    return parsed;
  }

  private int validNaturalInt(String i) {
    int parsed = Integer.parseInt(i);
    if (parsed < 0) {
      throw new NumberFormatException("Negative number");
    }
    return parsed;
  }
}
