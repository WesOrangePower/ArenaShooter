package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.RuleSet;
import agency.shitcoding.arena.storage.StorageProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class ArenaHostCmd extends CommandInst {
    public static final int ARG_RULESET = 1;
    public static final int ARG_ARENA = 2;
    public static final int ARG_MIN_LEN = 3;

    private RuleSet ruleSet;
    private Arena arena;

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
        game.addPlayer((Player) sender);
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
            sender.sendRichMessage("<red>Invalid rule set. Valid rule sets are: <yellow>" + Arrays.toString(RuleSet.values()));
            return false;
        }

        arena = StorageProvider.getArenaStorage().getArena(args[ARG_ARENA]);
        if (arena == null) {
            Collection<Arena> arenas = StorageProvider.getArenaStorage().getArenas();
            sender.sendRichMessage("<red>No such arena. Valid arenas are: " + arenas.stream().map(Arena::getName).collect(Collectors.joining(", ")));
            return false;
        }

        return true;
    }
}
