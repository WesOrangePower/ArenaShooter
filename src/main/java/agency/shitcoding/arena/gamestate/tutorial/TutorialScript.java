package agency.shitcoding.arena.gamestate.tutorial;

import static agency.shitcoding.arena.gamestate.announcer.AnnouncerConstant.*;

import agency.shitcoding.arena.gamestate.announcer.AnnouncementSkipProvider;
import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.LootPoint;
import agency.shitcoding.arena.models.Powerup;
import agency.shitcoding.arena.models.Weapon;
import java.time.Duration;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

@RequiredArgsConstructor
public class TutorialScript implements ArenaScript {
  private final Player player;
  private final TutorialGame game;
  private final AnnouncementSkipProvider skipProvider;

  @Override
  public void build(ScriptBuilder sb) {
    sb.setSkipProvider(skipProvider);
    sb.teleport(player, loc(8.45, -59.00, 8.69, -902.36, 2.77));
    sb.run(() -> player.getInventory().clear());
    sb.run(() -> player.getInventory().setItem(8, game.getExitItem(player)));
    sb.title(player, "tutorial.title.movement", "tutorial.subtitle.movement");
    sb.wait(Duration.ofSeconds(3));
    sb.run(() -> game.stage = 0);
    sb.message(player, "tutorial.script.introduction");
    sb.announceAndWait(player, TUTORIAL_INTRODUCTION);
    sb.fork(
        sb2 -> {
          sb2.setSkipProvider(skipProvider);
          sb2.waitUntilInside(player, BoundingBox.of(block(13, -55, 11), block(12, -53, 13)));
          sb2.mute(player);
          sb2.message(player, "tutorial.script.movement2");
          sb2.announceAndWait(player, TUTORIAL_MOVEMENT_2);
        });
    sb.message(player, "tutorial.script.movement1");
    sb.announceAndWait(player, TUTORIAL_MOVEMENT_1);
    sb.waitUntilInside(player, BoundingBox.of(block(17, -53, 3), block(27, -59, 13)));
    sb.mute(player);
    sb.title(player, "tutorial.title.startingWeapons", "tutorial.subtitle.startingWeapons");
    sb.run(() -> game.stage = 1);
    sb.wait(Duration.ofSeconds(3));
    sb.message(player, "tutorial.script.ammo1");
    sb.announceAndWait(player, TUTORIAL_AMMO_1);
    spawnAmmoLootPoints(sb);
    sb.message(player, "tutorial.script.weapon.gauntlet");
    sb.announceAndWait(player, TUTORIAL_AMMO_2);
    sb.powerup(player, Powerup.GAUNTLET);
    sb.announceAndWait(player, TUTORIAL_WEAPON_GAUNTLET);
    var ref = sb.spawnDummy(loc(22.52, -59.00, 4.59, -3599.99, -1.82));
    sb.waitUntilGone(ref);
    sb.wait(Duration.ofSeconds(1));
    sb.maxAmmo(player);
    sb.powerup(player, Powerup.MACHINE_GUN);
    sb.message(player, "tutorial.script.weapon.machinegun");
    sb.announceAndWait(player, TUTORIAL_WEAPON_MACHINEGUN);
    ref = sb.spawnDummy(loc(22.52, -59.00, 4.59, -3599.99, -1.82));
    sb.waitUntilGone(ref);
    sb.title(player, "tutorial.title.advancedWeapons", "tutorial.subtitle.advancedWeapons");
    sb.run(() -> game.stage = 2);
    for (Weapon weapon : Weapon.values()) {
      sb.powerup(player, weapon.getPowerUp());
    }
    sb.maxAmmo(player);
    sb.wait(Duration.ofSeconds(3));
    sb.message(player, "tutorial.script.weapon.shotgun");
    sb.announceAndWait(player, TUTORIAL_WEAPON_SHOTGUN);
    sb.message(player, "tutorial.script.weapon.rocketlauncher");
    sb.announceAndWait(player, TUTORIAL_WEAPON_ROCKETLAUNCHER);
    sb.message(player, "tutorial.script.ruleset.rof");
    sb.announceAndWait(player, TUTORIAL_RULESET_ROF);
    sb.message(player, "tutorial.script.weapon.plasmagun");
    sb.announceAndWait(player, TUTORIAL_WEAPON_PLASMAGUN);
    sb.message(player, "tutorial.script.weapon.lightninggun");
    sb.announceAndWait(player, TUTORIAL_WEAPON_LIGHTNINGGUN);
    sb.message(player, "tutorial.script.weapon.railgun");
    sb.announceAndWait(player, TUTORIAL_WEAPON_RAILGUN);
    sb.message(player, "tutorial.script.ruleset.instagib");
    sb.announceAndWait(player, TUTORIAL_RULESET_INSTAGIB);
    sb.message(player, "tutorial.script.weapon.bfg9k");
    sb.announceAndWait(player, TUTORIAL_WEAPON_BFG9K);
    sb.message(player, "tutorial.script.weapon1");
    sb.announceAndWait(player, TUTORIAL_WEAPON_1);
    var refs =
        sb.spawnDummies(
            loc(20.52, -59.00, 4.59, -3599.99, -1.82),
            loc(21.52, -59.00, 4.59, -3599.99, -1.82),
            loc(22.52, -59.00, 4.59, -3599.99, -1.82),
            loc(23.52, -59.00, 4.59, -3599.99, -1.82),
            loc(24.52, -59.00, 4.59, -3599.99, -1.82));
    sb.waitUntilGone(refs);
    sb.announceAndWait(player, IMPRESSIVE);
    sb.openDoor(player, "D_1");
    sb.waitUntilInside(player, BoundingBox.of(block(31, -54, 3), block(41, -59, 13)));
    sb.title(player, "tutorial.title.rocketJumping", "tutorial.subtitle.rocketJumping");
    sb.run(() -> game.stage = 3);
    sb.wait(Duration.ofSeconds(3));
    sb.message(player, "tutorial.script.rocketjumping1");
    sb.announceAndWait(player, TUTORIAL_ROCKETJUMPING_1);
    sb.powerup(player, Powerup.ROCKET_LAUNCHER);
    sb.maxAmmo(player);
    sb.waitUntilInside(player, BoundingBox.of(block(45, -52, 3), block(55, -59, 13)));
    sb.setHealth(player, 15d);
    spawnHealthLootPoints(sb);
    sb.message(player, "tutorial.script.powerups1");
    sb.announceAndWait(player, TUTORIAL_POWERUPS_1);
    sb.waitUntilHealthIsGreater(player, 19d);
    spawnArmorLootPoints(sb);
    sb.run(
        () ->
            generateLootPoints(List.of(lootPoint(loc(50.55, -59.00, 12.62), Powerup.MEGA_HEALTH))));
    sb.message(player, "tutorial.script.powerups2");
    sb.announceAndWait(player, TUTORIAL_POWERUPS_2);
    spawnMajorLootPoints(sb);
    sb.message(player, "tutorial.script.powerups3");
    sb.announceAndWait(player, TUTORIAL_POWERUPS_3);
    sb.message(player, "tutorial.script.conclusion");
    sb.announceAndWait(player, TUTORIAL_CONCLUSION);
    sb.announceAndWait(player, TUTORIAL_GOODLUCK);
    sb.wait(Duration.ofSeconds(5));
    sb.run(() -> game.endGame("tutorial.end", false));
  }

  private void spawnMajorLootPoints(ScriptBuilder sb) {
    var lps =
        List.of(
            lootPoint(loc(47.5, -59.0, 10.5), Powerup.PROTECTION),
            lootPoint(loc(47.5, -59.0, 6.5), Powerup.QUAD_DAMAGE));
    sb.run(() -> generateLootPoints(lps));
  }

  private void spawnArmorLootPoints(ScriptBuilder sb) {
    var lps =
        List.of(
            lootPoint(loc(54.5, -59.0, 12.5), Powerup.ARMOR_SHARD),
            lootPoint(loc(54.5, -59.0, 10.5), Powerup.LIGHT_ARMOR));
    sb.run(() -> generateLootPoints(lps));
  }

  private void spawnHealthLootPoints(ScriptBuilder sb) {
    var lps =
        List.of(
            lootPoint(loc(54.5, -59.0, 4.5), Powerup.STIM_PACK),
            lootPoint(loc(54.5, -59.0, 6.5), Powerup.MEDICAL_KIT));
    sb.run(() -> generateLootPoints(lps));
  }

  private void spawnAmmoLootPoints(ScriptBuilder sb) {
    var lps = new ArrayList<LootPoint>(Ammo.values().length);
    for (int i = 0; i < Ammo.values().length; i++) {
      lps.add(lootPoint(loc(26.5, -59.0, 4.5 + i * 2), Ammo.values()[i].getPowerup()));
    }
    sb.run(() -> generateLootPoints(lps));
  }

  private void generateLootPoints(Collection<LootPoint> lootPoints) {
    Optional.ofNullable(game.getLootManager())
        .ifPresentOrElse(
            lm -> lootPoints.forEach(lm::generateInstanceNoDelay),
            () -> {
              throw new IllegalStateException();
            });
  }

  private LootPoint lootPoint(Location loc, Powerup powerup) {
    return new LootPoint(UUID.randomUUID().toString(), loc, false, powerup, 0);
  }

  private Location block(int x, int y, int z) {
    return new Location(player.getWorld(), x, y, z).toBlockLocation();
  }

  @SuppressWarnings("SameParameterValue")
  private Location loc(double x, double y, double z) {
    return new Location(player.getWorld(), x, y, z);
  }

  @SuppressWarnings("SameParameterValue")
  private Location loc(double x, double y, double z, double yaw, double pitch) {
    return new Location(player.getWorld(), x, y, z, (float) yaw, (float) pitch);
  }
}
