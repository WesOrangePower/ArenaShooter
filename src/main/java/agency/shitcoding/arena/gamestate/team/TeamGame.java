package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.GameStage;
import agency.shitcoding.arena.models.RuleSet;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public abstract class TeamGame extends Game {
    protected TeamManager teamManager;

    protected TeamGame(Arena arena, RuleSet ruleSet) {
        super(arena, ruleSet);
        int maxPerTeam = ruleSet.getMaxPlayers() / 2;
        teamManager = new TeamManager(maxPerTeam);
    }



    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        if (gamestage == GameStage.IN_PROGRESS) {
            recalculateScore();
        }
    }

    public void addPlayer(Player player, ETeam team) {
        boolean added = teamManager.addToTeam(player, team);
        if (!added) {
            player.sendMessage("<dark_red>Команда переполнена!");
        }
        super.addPlayer(player);
    }

    @Override
    public void addPlayer(Player player) {
        throw new IllegalStateException("Use addPlayer(Player player, GameTeam team) instead");
    }

    public void assignTeam(Player p, ETeam team) throws RuntimeException {
        if (!teamManager.addToTeam(p, team)) {
            throw new RuntimeException("Team is full");
        }
    }

    public int getScoreForTeam(ETeam eTeam) {
        GameTeam team = teamManager.getTeam(eTeam);
        if (team instanceof PlayingTeam) {
            return ((PlayingTeam) team).getScore();
        }
        return 0;
    }

}
