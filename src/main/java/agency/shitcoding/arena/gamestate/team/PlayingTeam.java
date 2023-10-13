package agency.shitcoding.arena.gamestate.team;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.scoreboard.Team;

@Getter
@Setter
public abstract class PlayingTeam extends GameTeam {
    private Team scoreboardTeam;
    private int score;

    public int count() {
        return getPlayers().size();
    }
    public abstract String getDisplayName();
    public TextComponent getDisplayComponent() {
        return Component.text(getDisplayName()).color(getTextColor());
    }
    public abstract Color getBukkitColor();
    public TextColor getTextColor() {
        return TextColor.color(getBukkitColor().asRGB());
    }
}
