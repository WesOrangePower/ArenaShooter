package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.Lobby;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.storage.StorageProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ArenaHostCmd extends CommandInst {
    public static final int ARG_RULESET = 1;
    public static final int ARG_ARENA = 2;
    public static final int ARG_TEAM = 3;
    public static final int ARG_MIN_LEN = 3;

    private RuleSet ruleSet;
    private Arena arena;

    private ETeam team;

    public ArenaHostCmd(@NotNull CommandSender sender, @NotNull String[] args) {
        super(sender, args);
    }

    @Override
    public void execute() {
        if (validate()) {
            hostGame();
        }
    }

    private void hostGame() {
        if (!GameOrchestrator.getInstance().getGames().isEmpty()) {
            sender.sendRichMessage("<red>Игра уже создана. Присоединись к ней используя: <yellow><click:run_command:/arena join>/arena join</click>");
            return;
        }
        Game game = GameOrchestrator.getInstance().createGame(ruleSet, arena);
        if (game instanceof TeamGame teamGame) {
            teamGame.addPlayer((Player) sender, team);
        } else {
            game.addPlayer((Player) sender);
        }
        String message = String.format("<gold>%s <green>захостил <gold>%s <green>на карте <gold>%s<green>. " +
                        "Присоединись к ней используя: <yellow><click:run_command:/arena join>/arena join</click>",
                sender.getName(), ruleSet.getName(), arena.getName());
        Lobby.getInstance().getPlayersInLobby().forEach(player -> player.sendRichMessage(message));
    }

    private boolean validate() {
        if (args.length < ARG_MIN_LEN) {
            sender.sendRichMessage("<red>Invalid number of arguments. Usage: <yellow>/arena host <rule set> <arena>");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendRichMessage("<red>Only players can host games.");
            return false;
        }

        try {
            ruleSet = RuleSet.valueOf(args[ARG_RULESET].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendRichMessage("<red>Режим не найден. Используйте один из: <yellow>" + Arrays.toString(RuleSet.values()));
            return false;
        }

        arena = StorageProvider.getArenaStorage().getArena(args[ARG_ARENA]);
        if (arena == null) {
            Arena[] arenas = StorageProvider.getArenaStorage().getArenas().toArray(Arena[]::new);
            sender.sendRichMessage("<red>Арена не найдена. Используйте одну из: <yellow>" + Arrays.toString(arenas));
            return false;
        }

        if (!arena.isAllowHost() && !sender.hasPermission(ArenaDeathMatchCommand.ADMIN_PERM)) {
            sender.sendRichMessage("<red>В данный момент, хост этой арены доступен только администрации.");
            return false;
        }

        boolean isTeamGame = ruleSet.getGameRules().hasTeams();
        if (isTeamGame && args.length < ARG_TEAM + 1) {
            sender.sendRichMessage("<red>Командная игра. Выберите команду: <yellow>/arena host " + ruleSet.name() + " " + arena.getName() + " <команда><red>.");
            return false;
        }
        if (isTeamGame) {
            try {
                team = ETeam.valueOf(args[ARG_TEAM].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendRichMessage("<red>Invalid team. Valid teams are: <yellow>" + Arrays.toString(ETeam.values()));
                return false;
            }
        }

        return true;
    }
}
