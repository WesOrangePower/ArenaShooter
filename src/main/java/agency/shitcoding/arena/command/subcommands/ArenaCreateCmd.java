package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.storage.StorageProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ArenaCreateCmd extends CommandInst {
    public static final int ARG_NAME = 1;
    public static final int ARG_MIN_LEN = 2;
    public ArenaCreateCmd(@NotNull CommandSender sender, @NotNull String[] args) {
        super(sender, args);
    }

    @Override
    public void execute() {
        if (validate()) {
            createArena();
        }
    }

    private void createArena() {
        String name = args[ARG_NAME];
        Player player = (Player) sender;
        Arena arena = new Arena(name,
                player.getLocation().subtract(20, 20, 20),
                player.getLocation().add(20, 20, 20),
                new HashSet<>()
        );
        StorageProvider.getArenaStorage().storeArena(arena);
        sender.sendRichMessage("<green>Арена <yellow>" + name + "<green> создана");
    }

    private boolean validate() {
        String adminPerm = ArenaDeathMatchCommand.getAdminPerm();
        if (!sender.hasPermission(adminPerm)) {
            sender.sendRichMessage("<red>У вас нет прав на создание арены");
        }
        if (args.length < ARG_MIN_LEN) {
            sender.sendRichMessage("<red>Вы не указали название арены");
            return false;
        }
        int nameLen = args[ARG_NAME].length();
        if (nameLen > 16 || nameLen < 3) {
            sender.sendRichMessage("<red>Название арены должно быть от 3 до 16 символов");
            return false;
        }
        if (!args[ARG_NAME].matches("[a-zA-Z0-9_]{3,16}")) {
            sender.sendRichMessage("<red>Название арены должно состоять из букв и цифр");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendRichMessage("<red>Команду можно использовать только в игре");
            return false;
        }
        return true;
    }
}
