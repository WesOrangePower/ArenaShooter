package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import lombok.RequiredArgsConstructor;
import net.jellycraft.guiapi.api.InventorySize;
import net.jellycraft.guiapi.api.ItemSlot;
import net.jellycraft.guiapi.api.ViewRegistry;
import net.jellycraft.guiapi.api.ViewRenderer;
import net.jellycraft.guiapi.api.fluent.ItemBuilder;
import net.jellycraft.guiapi.api.fluent.ViewBuilder;
import net.jellycraft.guiapi.api.views.View;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

@RequiredArgsConstructor
public class ArenaMainMenu {
    public static final Component TITLE = Component.text("Арена", TextColor.color(0xaa2222), TextDecoration.BOLD);

    private final Player player;

    public void render() {
        View view = getView();
        ViewRenderer viewRenderer = new ViewRenderer(view);
        viewRenderer.render();
    }

    private View getView() {
        ViewBuilder viewBuilder = ViewBuilder.builder()
                .withTitle(TITLE)
                .withHolder(player)
                .withSize(InventorySize.DOUBLE_CHEST_NO_BOTTOM_ROW);

        ItemSlot[] itemSlots = { hostGameOrJoinButton(), settingsButton(), statsButton(), faqButton() };

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
                .withName(Component.text("Справка", TextColor.color(0xa94366)))
                .withLoreLine("Нажмите, чтобы открыть")
                .withLoreLine("справочник об Арене")
                .withClickAction(((clickType, clickContext) -> new FaqMenu(player).render()))
                .build();
    }

    private ItemSlot statsButton() {
        return soonTmButton("Статистика");
    }

    private ItemSlot settingsButton() {
        return soonTmButton("Настройки");
    }

    private ItemSlot soonTmButton(String name) {
        return ItemBuilder.builder()
                .withMaterial(Material.BARRIER)
                .withName(Component.text(name, TextColor.color(0xa94366)))
                .withLoreLine("Скоро будет")
                .build();
    }

    private ItemSlot hostGameOrJoinButton() {
        GameOrchestrator orchestrator = GameOrchestrator.getInstance();
        if (orchestrator.getGameByPlayer(player).isPresent()) {
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
        Component name = Component.text(game.getRuleSet().getName(), NamedTextColor.GREEN)
                .append(Component.text(" на ", NamedTextColor.GRAY))
                .append(Component.text(game.getArena().getName(), NamedTextColor.RED));

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
                .withLoreLine(Component.text("Игроков: " + game.getPlayers().size() + "/" + game.getRuleSet().getMaxPlayers()))
                .withLoreLine(Component.text(Arrays.toString(playerNames)))
                .withClickAction((type, ctx) -> arenaJoinClickAction(game))
                .build();

        // This is needed for some reason
        item.getItemStack().editMeta(m -> m.displayName(name));

        return item;
    }

    private void arenaJoinClickAction(Game game) {
        ViewRegistry.closeForPlayer(player);
        if (game instanceof TeamGame teamGame) {
            // TODO: implement team selection
            player.performCommand("arena join red");
        } else {
            player.performCommand("arena join");
        }
    }

    private ItemSlot hostGameButton() {
        return ItemBuilder.builder()
                .withMaterial(Material.DIAMOND_SWORD)
                .withName(Component.text("Создать игру", TextColor.color(0x22bb22)))
                .withLore(Component.text("Создать новую игру", TextColor.color(0x228822)))
                .withClickAction(((clickType, clickContext) -> new HostGameMenu(player).render()))
                .build();
    }

    private ItemSlot leaveButton() {
        return ItemBuilder.builder()
                .withMaterial(Material.ARROW)
                .withName(Component.text("Покинуть игру", TextColor.color(0xbb2222)))
                .withLore(Component.text("Покинуть текущую игру", TextColor.color(0x882222)))
                .withClickAction(((clickType, clickContext) -> player.performCommand("arena leave")))
                .build();
    }
}
