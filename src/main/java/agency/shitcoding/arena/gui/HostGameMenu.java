package agency.shitcoding.arena.gui;

import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.GameTeam;
import agency.shitcoding.arena.gamestate.team.PlayingTeam;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.storage.StorageProvider;
import lombok.RequiredArgsConstructor;
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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class HostGameMenu {
    private final Player player;
    Arena chosenArena;
    RuleSet chosenRuleSet;
    String chosenTeam;

    public void render() {
        chooseArena();
    }

    private void chooseArena() {
        PaginatedView view = ViewBuilder.builder()
                .withHolder(player)
                .withTitle("Выберите арену...")
                .build()
                .toPaginatedView()
                .withItems(getArenaItems())
                .build();

        new ViewRenderer(view).render();
    }

    private void chooseRuleSet() {
        PaginatedView view = ViewBuilder.builder()
                .withHolder(player)
                .withTitle("Выберите режим...")
                .build()
                .toPaginatedView()
                .withItems(getRuleSetItems())
                .build();

        new ViewRenderer(view).render();
    }

    private void execute() {
        ViewRegistry.closeForPlayer(player);
        if (chosenTeam == null) {
            player.performCommand(String.format("arena host %s %s", chosenRuleSet.name(), chosenArena.getName()));
            return;
        }
        player.performCommand(String.format("arena host %s %s %s", chosenRuleSet.name(), chosenArena.getName(), chosenTeam));
    }

    private List<Item> getArenaItems() {
        Collection<Arena> arenas = StorageProvider.getArenaStorage().getArenas();
        return arenas.stream()
                .filter(Arena::isAllowHost)
                .map(arena -> {
                            ItemSlot build = ItemBuilder.builder()
                                    .withMaterial(Material.IRON_SWORD)
                                    .withName(Component.text(arena.getName(), NamedTextColor.GOLD))
                                    .withClickAction(arenaClickAction(arena))
                                    .build();
                            return new Item(build.getItemStack(), build.getOverrideClickAction());
                        }
                )
                .toList();
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
                                    .withMaterial(Material.GOLDEN_PICKAXE)
                                    .withName(Component.text(ruleSet.getName(), NamedTextColor.GOLD))
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
                .withHolder(player)
                .withTitle("Выберите команду...")
                .build()
                .toPaginatedView()
                .withItems(getTeamItems())
                .build();

        new ViewRenderer(view).render();
    }

    private List<Item> getTeamItems() {
        return Arrays.stream(ETeam.values())
                .map(team -> {
                            GameTeam gameTeam = null;
                            try {
                                gameTeam = team.getTeamClass().getConstructor().newInstance();
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                     NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            }
                            if (gameTeam instanceof PlayingTeam playingTeam) {
                                ItemSlot build = ItemBuilder.builder()
                                        .withMaterial(team.getIcon())
                                        .withClickAction(teamClickAction(team))
                                        .build();
                                ItemStack itemStack = build.getItemStack();
                                itemStack.editMeta(meta -> meta.displayName(playingTeam.getDisplayComponent()));
                                return new Item(itemStack, build.getOverrideClickAction());
                            }
                            return null;
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
