package agency.shitcoding.arena.gamestate.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.jspecify.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum ETeam {
  RED (RedTeam.class, Material.RED_WOOL, new RedTeamMeta()),
  BLUE (BlueTeam.class, Material.BLUE_WOOL, new BlueTeamMeta());

  private final Class<? extends GameTeam> teamClass;
  private final Material icon;
  private final TeamMeta teamMeta;

  public static @Nullable ETeam getTeamByMaterial(Material type) {
    for (ETeam team : values()) {
      if (team.icon.equals(type)) {
        return team;
      }
    }
    return null;
  }

  public @Nullable ETeam getTeamByClass(Class<? extends GameTeam> teamClass) {
    for (ETeam team : values()) {
      if (team.teamClass.equals(teamClass)) {
        return team;
      }
    }
    return null;
  }
}

