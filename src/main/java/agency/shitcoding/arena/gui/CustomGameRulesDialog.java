package agency.shitcoding.arena.gui;


import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.CustomGameRulesBuilder;
import agency.shitcoding.arena.models.GameRules;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.event.ClickCallback;

@SuppressWarnings("UnstableApiUsage")
public class CustomGameRulesDialog {
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
  public CustomGameRulesDialog(
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
    var dialog =
        Dialog.create(
            c ->
                c.empty()
                    .base(
                        DialogBase.builder(langPlayer.getRichLocalized("menu.customRules.title"))
                            .inputs(getDialogInputs())
                            .build())
                    .type(DialogType.confirmation(yesButton(), noButton())));

    langPlayer.getPlayer().showDialog(dialog);
  }

  private static final String GAME_LENGTH_SECONDS = "game_length_seconds"; // Actually minutes
  private static final String MIN_PLAYERS = "min_players";
  private static final String MAX_PLAYERS = "max_players";
  private static final String DROP_MOST_VALUABLE_WEAPON_ON_DEATH =
      "drop_most_valuable_weapon_on_death";
  private static final String FAST_WEAPON_SPAWN = "fast_weapon_spawn";
  private static final String SHOW_HEALTH = "show_health";

  @SuppressWarnings("DataFlowIssue")
  private ActionButton yesButton() {
    return ActionButton.builder(langPlayer.getRichLocalized("menu.customRules.confirm"))
        .action(
            DialogAction.customClick(
                (view, audience) -> {
                  var gameLenMinutes = Math.round(view.getFloat(GAME_LENGTH_SECONDS));
                  var minPlayers = Math.round(view.getFloat(MIN_PLAYERS));
                  var maxPlayers = Math.round(view.getFloat(MAX_PLAYERS));
                  var dropMostValuableWeaponOnDeath =
                      view.getBoolean(DROP_MOST_VALUABLE_WEAPON_ON_DEATH);
                  var fastWeapon = view.getBoolean(FAST_WEAPON_SPAWN);

                  customGameRulesBuilder.setGameLengthSeconds(gameLenMinutes * 60L);
                  customGameRulesBuilder.setMinPlayers(minPlayers);
                  customGameRulesBuilder.setMaxPlayers(maxPlayers);
                  customGameRulesBuilder.setDropMostValuableWeaponOnDeath(
                      dropMostValuableWeaponOnDeath);
                  customGameRulesBuilder.setFastWeaponSpawn(fastWeapon);

                  onGameRulesChanged.accept(customGameRulesBuilder.build());
                },
                ClickCallback.Options.builder().build()))
        .build();
  }

  private ActionButton noButton() {
    return ActionButton.builder(langPlayer.getRichLocalized("menu.customRules.discard"))
        .action(
            DialogAction.customClick(
                (view, audience) -> onDiscard.run(),
                ClickCallback.Options.builder().build()))
        .build();
  }

  private List<? extends DialogInput> getDialogInputs() {
    return List.of(
        DialogInput.numberRange(
                GAME_LENGTH_SECONDS,
                langPlayer.getRichLocalized("menu.customRules.gameLengthSeconds"),
                1,
                60)
            .initial((float) (customGameRulesBuilder.getGameLengthSeconds() / 60))
            .step(1f)
            .build(),
        DialogInput.numberRange(
                MIN_PLAYERS, langPlayer.getRichLocalized("menu.customRules.minPlayers"), 1, 50)
            .initial(Float.valueOf(customGameRulesBuilder.getMinPlayers()))
            .step(1f)
            .build(),
        DialogInput.numberRange(
                MAX_PLAYERS, langPlayer.getRichLocalized("menu.customRules.maxPlayers"), 1, 50)
            .initial(Float.valueOf(customGameRulesBuilder.getMaxPlayers()))
            .step(1f)
            .build(),
        DialogInput.bool(
                DROP_MOST_VALUABLE_WEAPON_ON_DEATH,
                langPlayer.getRichLocalized("menu.customRules.dropMostValuableWeaponOnDeath"))
            .initial(customGameRulesBuilder.getDropMostValuableWeaponOnDeath())
            .build(),
        DialogInput.bool(
                FAST_WEAPON_SPAWN, langPlayer.getRichLocalized("menu.customRules.fastWeaponSpawn"))
            .initial(customGameRulesBuilder.getFastWeaponSpawn())
            .build(),
        DialogInput.bool(SHOW_HEALTH, langPlayer.getRichLocalized("menu.customRules.showHealth"))
            .initial(customGameRulesBuilder.getShowHealth())
            .build());
  }
}
