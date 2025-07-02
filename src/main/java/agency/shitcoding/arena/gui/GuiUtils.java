package agency.shitcoding.arena.gui;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.jellycraft.guiapi.api.ItemSlot;

public interface GuiUtils {
  @SuppressWarnings("UnstableApiUsage")
  static ItemSlot addModelData(String name, ItemSlot is) {
    var cmd = CustomModelData.customModelData()
        .addString(name)
        .build();
    is.getItemStack().setData(DataComponentTypes.CUSTOM_MODEL_DATA, cmd);
    return is;
  }
}
