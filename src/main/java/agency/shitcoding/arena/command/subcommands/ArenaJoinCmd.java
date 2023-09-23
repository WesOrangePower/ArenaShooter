package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ArenaJoinCmd extends CommandInst {

    public ArenaJoinCmd(@NotNull CommandSender sender, @NotNull String[] args) {
        super(sender, args);
    }

    @Override
    public void execute() {
        if (sender instanceof Player player) {
            GameOrchestrator gameOrchestrator = GameOrchestrator.getInstance();
            if (args.length >= 2
                    && args[1].equalsIgnoreCase("forceStart")
                    && player.hasPermission(ArenaDeathMatchCommand.ADMIN_PERM)
            ) {
                forceStart(gameOrchestrator, player);
                return;
            }
            if (gameOrchestrator.getGameByPlayer(player).isPresent()) {
                sender.sendRichMessage("<dark_red>Вы уже в игре");
                return;
            }
            Optional<Game> first = gameOrchestrator.getGames().stream().findFirst();
            if (first.isPresent()) {
                Game game = first.get();
                game.addPlayer(player);
                return;
            }
            sender.sendRichMessage("<dark_red>Нет доступных игр. Используйте /arena host");
            return;
        }
        sender.sendRichMessage("<dark_red> Only players can use this command");
    }

    private void forceStart(GameOrchestrator gameOrchestrator, Player player) {
        gameOrchestrator.getGameByPlayer(player)
                .ifPresent(Game::startGame);
    }

}
