package agency.shitcoding.arena.gamestate.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
@Getter
public enum ETeam {
    RED(RedTeam.class, Material.RED_WOOL),
    BLUE(BlueTeam.class, Material.BLUE_WOOL);
//    SPECTATOR(SpectatorTeam.class, Material.GLASS);

    private final Class<? extends GameTeam> teamClass;
    private final Material icon;
    public ETeam getTeamByClass(Class<? extends GameTeam> teamClass) {
        for (ETeam team : values()) {
            if (team.teamClass.equals(teamClass)) {
                return team;
            }
        }
        return null;
    }
}

