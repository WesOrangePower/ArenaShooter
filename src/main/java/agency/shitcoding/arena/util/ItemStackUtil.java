package agency.shitcoding.arena.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.jellycraft.guiapi.api.ItemSlot;
import org.bukkit.inventory.ItemStack;

public interface ItemStackUtil {
  @SuppressWarnings("UnstableApiUsage")
  static ItemStack addModelData(String s, ItemStack is) {
    var cmd = CustomModelData.customModelData()
        .addString(s)
        .build();
    is.setData(DataComponentTypes.CUSTOM_MODEL_DATA, cmd);
    return is;
  }

  static ItemSlot addModelData(String s, ItemSlot is) {
    addModelData(s, is.getItemStack());
    return is;
  }
}
