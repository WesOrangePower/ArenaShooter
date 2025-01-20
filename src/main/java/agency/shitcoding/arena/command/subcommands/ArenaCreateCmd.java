package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.storage.StorageProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

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
        List.of(),
        player.getLocation().subtract(20, 20, 20),
        player.getLocation().add(20, 20, 20),
        new HashSet<>(),
        new HashSet<>(),
        new HashSet<>(),
        new HashSet<>(),
        new HashSet<>(),
        true,
        new HashSet<>(),
        new HashSet<>()
    );
    StorageProvider.getArenaStorage().storeArena(arena);
    sender.sendRichMessage("<green>Arena <yellow>" + name + "<green> created");
  }

  private boolean validate() {
    String adminPerm = ArenaDeathMatchCommand.getAdminPerm();
    if (!sender.hasPermission(adminPerm)) {
      sender.sendRichMessage("<dark_red>You don't have permission to access this command");
      return false;
    }
    if (args.length < ARG_MIN_LEN) {
      sender.sendRichMessage("<dark_red>Please provide a name for the arena");
      return false;
    }
    int nameLen = args[ARG_NAME].length();
    if (nameLen > 16 || nameLen < 3) {
      sender.sendRichMessage("<dark_red>Name must be between 3 and 16 characters");
      return false;
    }
    if (!args[ARG_NAME].matches("\\w{3,16}")) {
      sender.sendRichMessage("<dark_red>Name must contain only letters, numbers and underscores");
      return false;
    }
    if (!(sender instanceof Player)) {
      sender.sendRichMessage("<dark_red>This command can only be executed by a player");
      return false;
    }
    return true;
  }
}
