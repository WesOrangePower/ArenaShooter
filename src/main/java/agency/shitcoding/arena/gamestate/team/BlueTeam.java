package agency.shitcoding.arena.gamestate.team;

import org.bukkit.Color;

public class BlueTeam extends PlayingTeam {
    @Override
    public String getDisplayName() {
        return "Синие";
    }

    @Override
    public Color getBukkitColor() {
        return Color.BLUE;
    }
}
