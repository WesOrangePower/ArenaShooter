package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.WeaponItemGenerator;
import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.listeners.ShotgunListener;
import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.Bukkit;
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
        case "shotgun" -> {
          ShotgunListener.tracers = !ShotgunListener.tracers;
          sender.sendMessage("Shotgun tracers: " + ShotgunListener.tracers);
        }
        case "guns" -> {
          Player p = (Player) sender;
          if (args.length == 3) {
            p = Bukkit.getPlayer(args[2]);
          }
          if (p == null) {
            sender.sendMessage(args[2] + ": null");
            return;
          }
          for (Weapon weapon : Weapon.values()) {
            p.getInventory().setItem(weapon.slot, CosmeticsService.getInstance().getWeapon(p, weapon));
          }
          Ammo.maxAmmoForPlayer(p);
          sender.sendMessage("Given");
        }
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
