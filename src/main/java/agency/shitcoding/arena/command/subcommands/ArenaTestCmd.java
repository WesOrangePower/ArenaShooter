package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ArenaTestCmd extends CommandInst {

  public ArenaTestCmd(@NotNull CommandSender sender, @NotNull String[] args) {
    super(sender, args);
  }

  @Override
  public void execute() {
    if (validate()) {
      switch (args[1].toLowerCase()) {
        case "gib" -> new GameDamageEvent((Player) sender, (Player) sender, 1000d, Weapon.SHOTGUN)
            .fire();
      }
    }
  }

  private boolean validate() {
    String adminPerm = ArenaDeathMatchCommand.getAdminPerm();
    if (!sender.hasPermission(adminPerm)) {
      sender.sendRichMessage("<dark_red>У вас нет прав на использование этой подкоманды.");
      return false;
    }
    return true;
  }
}
