package agency.shitcoding.arena.gamestate.team;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;

public final class RedTeamMeta extends ColoredArmorTeamMeta {

  public RedTeamMeta() {
    super(Color.RED);
  }

  @Override
  public String getDisplayName() {
    return "team.red";
  }

  @Override
  public Color getBukkitColor() {
    return Color.RED;
  }

  @Override
  public TextColor getTextColor() {
    return NamedTextColor.RED;
  }

  @SuppressWarnings("deprecation")
  @Override
  public ChatColor getChatColor() {
    return ChatColor.RED;
  }
}
