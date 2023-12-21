package agency.shitcoding.arena.gamestate.team;

import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.GameStage;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.models.exceptions.TeamFullException;
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
    public String youJoinedGameMessage(Player player) {
        PlayingTeam team = (PlayingTeam) teamManager.getTeam(player).orElseThrow();
        return "<green> + вы присоединились к команде <color:" + team.getTextColor().asHexString() + ">" + team.getDisplayName();
    }

    @Override
    public String joinBroadcastMessage(Player player) {
        PlayingTeam team = (PlayingTeam) teamManager.getTeam(player).orElseThrow();
        return String.format("<green>%s присоединился к команде <color:#%s>%s",
                player.getName(),
                Integer.toString(team.getBukkitColor().asRGB(), 16),
                team.getDisplayName());
    }

    @Override
    public String leaveBroadcastMessage(Player player) {
        return super.leaveBroadcastMessage(player);
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

    public void assignTeam(Player p, ETeam team) throws TeamFullException {
        if (!teamManager.addToTeam(p, team)) {
            throw new TeamFullException();
        }
    }

    public int getScoreForTeam(ETeam eTeam) {
        GameTeam team = teamManager.getTeam(eTeam);
        if (team instanceof PlayingTeam playingTeam) {
            return playingTeam.getScore();
        }
        return 0;
    }

}
