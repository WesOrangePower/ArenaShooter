package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.localization.LangContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

public interface TeamMeta {
  ItemStack getHelmet();
  ItemStack getChest();
  ItemStack getLeggings();
  ItemStack getBoots();
  String getDisplayName();
  Color getBukkitColor();
  TextColor getTextColor();
  @SuppressWarnings("deprecation")
  ChatColor getChatColor();

  default Component getDisplayComponent(LangContext langContext) {
    return Component.text(langContext.getLocalized(getDisplayName())).color(getTextColor());
  }
}
