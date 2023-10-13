package agency.shitcoding.arena.gamestate.team;

import org.bukkit.Color;

public class RedTeam extends PlayingTeam {
    @Override
    public String getDisplayName() {
        return "Красные";
    }

    @Override
    public Color getBukkitColor() {
        return Color.RED;
    }
}
