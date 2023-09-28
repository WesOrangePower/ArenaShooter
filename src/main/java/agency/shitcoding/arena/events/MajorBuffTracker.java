package agency.shitcoding.arena.events;

import agency.shitcoding.arena.gamestate.GameOrchestrator;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Optional;


@Getter
@Setter
public class MajorBuffTracker {
    private Integer quadDamageTicks = null;
    private Integer protectionTicks = null;

    public Team getQuadDamageTeam() {
        Scoreboard scoreboard = GameOrchestrator.getInstance().getScoreboard();
        return Optional.ofNullable(scoreboard.getTeam("powerup-quadDamage")).orElseGet(() -> {
            Team quadDamageTeam = scoreboard.registerNewTeam("powerup-quadDamage");
            quadDamageTeam.color(NamedTextColor.BLUE);
            return quadDamageTeam;
        });
    }

    public Team getProtectionTeam() {
        Scoreboard scoreboard = GameOrchestrator.getInstance().getScoreboard();
        return Optional.ofNullable(scoreboard.getTeam("powerup-protection")).orElseGet(() -> {
            Team quadDamageTeam = scoreboard.registerNewTeam("powerup-protection");
            quadDamageTeam.color(NamedTextColor.GREEN);
            return quadDamageTeam;
        });
    }
}
