package agency.shitcoding.arena.gamestate.team;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class GameTeam {
    private final Set<Player> players = new HashSet<>();

    protected GameTeam() {
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(this.getClass());
    }
}
