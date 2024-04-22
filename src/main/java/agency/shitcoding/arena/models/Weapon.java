package agency.shitcoding.arena.models;

import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public enum Weapon {
  GAUNTLET("weapon.gauntlet", Color.BLUE, 0, Material.WOODEN_SWORD, Ammo.BULLETS, 0, 3), MACHINE_GUN("weapon.machineGun", Color.YELLOW, 1, Material.STONE_PICKAXE, Ammo.BULLETS, 2, 3),
  LIGHTNING_GUN("weapon.lightningGun", Color.WHITE, 5, Material.IRON_PICKAXE, Ammo.LIGHTNING, 2, 3),
  SHOTGUN("weapon.shotGun", Color.ORANGE, 2, Material.WOODEN_PICKAXE, Ammo.SHELLS, 2, 16),
  ROCKET_LAUNCHER("weapon.rocketLauncher", Color.RED, 3, Material.BOW, Ammo.ROCKETS, 1, 20),
  RAILGUN("weapon.railgun", Color.AQUA, 6, Material.CROSSBOW, Ammo.CELLS, 20, 30),
  PLASMA_GUN("weapon.plasmaGun", Color.LIME, 4, Material.GOLDEN_HOE, Ammo.CELLS, 10, 4),
  BFG9K("weapon.bfg9k", Color.LIME, 7, Material.DIAMOND_HOE, Ammo.LIGHTNING, 200, 60);

  public final String translatableName;
  public final Color color;
  public final int slot;
  public final Material item;
  public final Ammo ammo;
  public final int ammoPerShot;
  public final int cooldown;

  public static void applyCooldown(Player player, int ticks) {
    Arrays.stream(values())
        .map(w -> w.item)
        .forEach(m -> player.setCooldown(m, ticks));
  }

  public static Optional<Weapon> getWeapon(ItemStack itemStack) {
    return Arrays.stream(values())
        .filter(w -> w.item.equals(itemStack.getType()))
        .findFirst();
  }
}
