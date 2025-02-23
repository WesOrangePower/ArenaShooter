package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.ArenaCommand;
import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.listeners.GauntletListener;
import agency.shitcoding.arena.events.listeners.RailListener;
import agency.shitcoding.arena.events.listeners.ShotgunListener;
import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.Powerup;
import agency.shitcoding.arena.models.Weapon;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ArenaUtilsCmd extends CommandInst {

  public ArenaUtilsCmd(@NotNull CommandSender sender, @NotNull String[] args) {
    super(sender, args);
  }

  @Override
  public void execute() {
    if (validate()) {
      switch (args[1].toLowerCase()) {
        case "gib" ->
            new GameDamageEvent((Player) sender, (Player) sender, 1000d, Weapon.SHOTGUN).fire();
        case "helix" -> {
          RailListener.helix = !RailListener.helix;
          sender.sendMessage("Rail helix: " + RailListener.helix);
        }
        case "cutter" -> {
          GauntletListener.bloodParticles = !GauntletListener.bloodParticles;
          sender.sendMessage("Gauntlet blood particles: " + GauntletListener.bloodParticles);
        }
        case "tracers" -> {
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
            p.getInventory()
                .setItem(weapon.slot, CosmeticsService.getInstance().getWeapon(p, weapon));
          }
          Ammo.maxAmmoForPlayer(p);
          sender.sendMessage("Given");
        }
        case "powerup" -> {
          Player p = (Player) sender;
          if (args.length == 4) {
            p = Bukkit.getPlayer(args[3]);
          }
          if (p == null) {
            sender.sendMessage(args[3] + ": null");
            return;
          }
          try {
            var powerup = Powerup.valueOf(args[2].toUpperCase());
            powerup.getOnPickup().apply(p);
          } catch (IllegalArgumentException e) {
            sender.sendMessage("Invalid powerup: " + args[2]);
            sender.sendMessage("Available powerups: " + Arrays.toString(Powerup.values()));
          }
        }
      }
    }
  }

  private boolean validate() {
    String adminPerm = ArenaCommand.getAdminPerm();
    if (!sender.hasPermission(adminPerm)) {
      sender.sendRichMessage("<dark_red>You don't have permission to access this command.");
      return false;
    }
    return true;
  }
}
