package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.ArenaShooter;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Getter
public class TeamManager {
    private final int maxPerTeam;
    private final Map<ETeam, GameTeam> teams;

    public TeamManager(int maxPerTeam) {
        this.maxPerTeam = maxPerTeam;
        teams = new HashMap<>();
        try {
            for (ETeam value : ETeam.values()) {
                var nac = value.getTeamClass().getConstructor();
                teams.put(value, nac.newInstance());
            }
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            ArenaShooter.getInstance().getLogger()
                            .severe("Failed to instantiate TeamManager, " +
                                    "check if all teams have a no-args constructor");
            throw new RuntimeException("Failed to instantiate teams");
        }
    }

    /**
     * Adds player to team
     * @return false if full, true if successful
     */
    public boolean addToTeam(Player player, ETeam gameTeam) {
        Set<Player> teamPlayers = teams.get(gameTeam).getPlayers();
        if (teamPlayers.size() >= maxPerTeam && !player.hasPermission("arena.fullTeamJoin")) {
            return false;
        }
        teamPlayers.add(player);
        return true;
    }

    public GameTeam getTeam(ETeam team) {
        return teams.get(team);
    }

    public Optional<GameTeam> getTeam(Player player) {
        for (GameTeam team : teams.values()) {
            if (team.getPlayers().contains(player)) {
                return Optional.of(team);
            }
        }
        return Optional.empty();
    }
}
