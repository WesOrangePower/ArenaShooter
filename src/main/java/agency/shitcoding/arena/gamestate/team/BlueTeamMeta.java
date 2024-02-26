package agency.shitcoding.arena.gamestate.team;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;

public final class BlueTeamMeta extends ColoredArmorTeamMeta {

  public BlueTeamMeta() {
    super(Color.BLUE);
  }

  @Override
  public String getDisplayName() {
    return "Синие";
  }

  @Override
  public Color getBukkitColor() {
    return Color.BLUE;
  }

  @Override
  public TextColor getTextColor() {
    return NamedTextColor.BLUE;
  }

  @Override
  @SuppressWarnings("deprecation")
  public ChatColor getChatColor() {
    return ChatColor.BLUE;
  }

}
