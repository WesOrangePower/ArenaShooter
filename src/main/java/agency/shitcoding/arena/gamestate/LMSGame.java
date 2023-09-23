package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.GameStage;
import agency.shitcoding.arena.models.RuleSet;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class LMSGame extends Game {
    public LMSGame(Arena arena) {
        super(arena, RuleSet.LMS);
    }

    @Override
    protected void startGameStage2() {

    }

    @Override
    public void onPlayerDeath(Player player) {
        super.onPlayerDeath(player);
        if (getGamestage() == GameStage.IN_PROGRESS) {
            checkLms();
        }
    }



    public void checkLms() {
        Set<Player> playersThatNeverDied = getPlayers().stream()
                .filter(p -> !getDiedOnce().contains(p))
                .collect(Collectors.toSet());

        if (playersThatNeverDied.size() == 1) {
            Player player = playersThatNeverDied.stream().findFirst().orElseThrow();
            endGame("Победил " + player.getName());
        }
    }
}
