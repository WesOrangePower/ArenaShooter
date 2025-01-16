package agency.shitcoding.arena.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jellycraft.guiapi.api.ClickAction;
import net.jellycraft.guiapi.api.ClickContext;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class TextInputClickAction implements ClickAction {
  @Getter private final static Map<Player, TextInputClickAction> registry = new HashMap<>();
  private final String prompt;
  private final Consumer<String> callback;

  @Override
  public void onClick(ClickType clickType, ClickContext clickContext) {
    Player holder = clickContext.getCurrentView().getInventoryHolder();
    Inventory inventory = Bukkit.createInventory(holder, InventoryType.ANVIL);
    ItemStack paper = new ItemStack(Material.PAPER);
    paper.editMeta(meta -> meta.displayName(Component.text(prompt)));
    inventory.setItem(0, paper);
    registry.put(holder, this);
    holder.openInventory(inventory);
  }

  public void resolve(Player player, String input) {
    registry.remove(player);
    callback.accept(input);
  }
}
