package agency.shitcoding.arena.gamestate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Data
@AllArgsConstructor
public class PlayerScore implements Comparable<PlayerScore>{
    private int score;
    private Player player;

    @Override
    public int compareTo(@NotNull PlayerScore o) {
        return Integer.compare(o.score, this.score); // Reversed
    }
}
