package agency.shitcoding.arena.models;

import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.WeaponItemGenerator;
import agency.shitcoding.arena.events.MajorBuffTracker;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;
import java.util.function.Function;

import static agency.shitcoding.arena.GameplayConstants.*;

@RequiredArgsConstructor
@Getter
public enum Powerup {

  MEGA_HEALTH(
      "<gradient:#a64dff:#8c1aff>Mega health</gradient>",
      PowerupType.BUFF,
      new ItemStack(Material.PUFFERFISH),
      player -> {
        var attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) {
          return false;
        }
        attribute.setBaseValue(MEGA_HEALTH_VALUE);
        player.setLevel(MAX_ARMOR);
        player.setHealth(MEGA_HEALTH_VALUE);
        return true;
      },
      MEGA_HEALTH_SPAWN_INTERVAL_TICKS,
      MEGA_HEALTH_SPAWN_OFFSET_TICKS
  ),
  /**
   * Buff Heals the player for 2 hp (1 heart)
   */
  STIM_PACK(
      "<gradient:#11900a:#01b817>хилку</gradient>",
      PowerupType.BUFF,
      potionItem(Color.BLUE),
      player -> genericHealth(player, 2),
      STIM_PACK_SPAWN_INTERVAL_TICKS,
      STIM_PACK_SPAWN_OFFSET_TICKS
  ),
  /**
   * Buff Heals the player for 5 hp (2.5 hearts)
   */
  MEDICAL_KIT(
      "<gradient:#d64343:#ec3f22>аптечку</gradient>",
      PowerupType.BUFF,
      potionItem(Color.RED),
      player -> genericHealth(player, 5),
      MEDIKIT_SPAWN_INTERVAL_TICKS,
      MEDIKIT_SPAWN_OFFSET_TICKS
  ),
  ROCKET_BOX(
      "<red>коробку ракет</red>",
      PowerupType.AMMO,
      new ItemStack(Material.RED_SHULKER_BOX),
      player -> giveAmmo(player, Ammo.ROCKETS, 5),
      AMMO_DROP_SPAWN_INTERVAL_TICKS,
      AMMO_DROP_SPAWN_OFFSET_TICKS
  ),
  LIGHTNING_BOX(
      "<white>коробку молнии</white>",
      PowerupType.AMMO,
      new ItemStack(Material.WHITE_SHULKER_BOX),
      player -> giveAmmo(player, Ammo.LIGHTNING, 200),
      AMMO_DROP_SPAWN_INTERVAL_TICKS,
      AMMO_DROP_SPAWN_OFFSET_TICKS
  ),
  BULLET_BOX(
      "<yellow>коробку пуль</yellow>",
      PowerupType.AMMO,
      new ItemStack(Material.YELLOW_SHULKER_BOX),
      player -> giveAmmo(player, Ammo.BULLETS, 50),
      AMMO_DROP_SPAWN_INTERVAL_TICKS,
      AMMO_DROP_SPAWN_OFFSET_TICKS
  ),
  SHELL_BOX(
      "<gold>коробку дробовых</gold>",
      PowerupType.AMMO,
      new ItemStack(Material.ORANGE_SHULKER_BOX),
      player -> giveAmmo(player, Ammo.SHELLS, 6),
      AMMO_DROP_SPAWN_INTERVAL_TICKS,
      AMMO_DROP_SPAWN_OFFSET_TICKS
  ),
  CELL_BOX(
      "<aqua>батарею</aqua>",
      PowerupType.AMMO,
      new ItemStack(Material.CYAN_SHULKER_BOX),
      player -> giveAmmo(player, Ammo.CELLS, 50),
      AMMO_DROP_SPAWN_INTERVAL_TICKS,
      AMMO_DROP_SPAWN_OFFSET_TICKS
  ),
  QUAD_DAMAGE(
      "<i><aqua>quad damage</aqua></i>",
      PowerupType.MAJOR_BUFF,
      new ItemStack(Material.NETHER_STAR),
      player -> {
        Optional<Game> gameByPlayer = GameOrchestrator.getInstance().getGameByPlayer(player);

        Integer quadDamageTicks = gameByPlayer
            .map(Game::getMajorBuffTracker)
            .map(MajorBuffTracker::getQuadDamageTicks)
            .orElse(QUAD_DAMAGE_DURATION);

        gameByPlayer.map(Game::getMajorBuffTracker)
            .ifPresent(mbt -> mbt.setQuadDamageTicks(null));

        var effectDamage = new PotionEffect(QUAD_DAMAGE_POTION_EFFECT, quadDamageTicks, 4);
        var effectGlowing = new PotionEffect(PotionEffectType.GLOWING, quadDamageTicks, 0);
        player.addPotionEffect(effectDamage);
        player.addPotionEffect(effectGlowing);
        return true;
      },
      QUAD_DAMAGE_SPAWN_INTERVAL_TICKS,
      QUAD_DAMAGE_SPAWN_OFFSET_TICKS
  ),
  PROTECTION(
      "<i><green>protection</green></i>",
      PowerupType.MAJOR_BUFF,
      new ItemStack(Material.DIAMOND_CHESTPLATE),
      player -> {
        Optional<Game> gameByPlayer = GameOrchestrator.getInstance().getGameByPlayer(player);

        Integer protectionTicks = gameByPlayer
            .map(Game::getMajorBuffTracker)
            .map(MajorBuffTracker::getProtectionTicks)
            .orElse(PROTECTION_DURATION);

        gameByPlayer.map(Game::getMajorBuffTracker)
            .ifPresent(mbt -> mbt.setProtectionTicks(null));

        var effectResistance = new PotionEffect(PROTECTION_POTION_EFFECT, protectionTicks, 1);
        var effectGlowing = new PotionEffect(PotionEffectType.GLOWING, protectionTicks, 0);
        player.addPotionEffect(effectResistance);
        player.addPotionEffect(effectGlowing);
        return true;
      },
      GameplayConstants.PROTECTION_SPAWN_INTERVAL_TICKS,
      GameplayConstants.PROTECTION_SPAWN_OFFSET_TICKS
  ),
  SHOTGUN(
      "<gold>дробовик</gold>",
      PowerupType.WEAPON,
      new ItemStack(Weapon.SHOTGUN.item),
      player -> giveWeaponOrAmmo(player, Weapon.SHOTGUN),
      SHOTGUN_SPAWN_INTERVAL_TICKS,
      SHOTGUN_SPAWN_OFFSET_TICKS
  ),
  ROCKET_LAUNCHER(
      "<red>ракетницу</red>",
      PowerupType.WEAPON,
      new ItemStack(Weapon.ROCKET_LAUNCHER.item),
      player -> giveWeaponOrAmmo(player, Weapon.ROCKET_LAUNCHER),
      ROCKET_LAUNCHER_SPAWN_INTERVAL_TICKS,
      ROCKET_LAUNCHER_SPAWN_OFFSET_TICKS
  ),
  LIGHTNING_GUN(
      "<white>громовержец</white>",
      PowerupType.WEAPON,
      new ItemStack(Weapon.LIGHTNING_GUN.item),
      player -> giveWeaponOrAmmo(player, Weapon.LIGHTNING_GUN),
      LIGHTNING_GUN_SPAWN_INTERVAL_TICKS,
      LIGHTNING_GUN_SPAWN_OFFSET_TICKS
  ),
  RAILGUN(
      "<aqua>рельсотрон</aqua>",
      PowerupType.WEAPON,
      new ItemStack(Weapon.RAILGUN.item),
      player -> giveWeaponOrAmmo(player, Weapon.RAILGUN),
      RAILGUN_SPAWN_INTERVAL_TICKS,
      RAILGUN_SPAWN_OFFSET_TICKS
  ),
  PLASMA_GUN(
      "<color:#00cc00>плазмострел</color>",
      PowerupType.WEAPON,
      new ItemStack(Weapon.PLASMA_GUN.item),
      player -> giveWeaponOrAmmo(player, Weapon.PLASMA_GUN),
      PLASMA_GUN_SPAWN_INTERVAL_TICKS,
      PLASMA_GUN_SPAWN_OFFSET_TICKS
  ),
  MACHINE_GUN(
      "<yellow>пулемёт</yellow>",
      PowerupType.WEAPON,
      new ItemStack(Weapon.MACHINE_GUN.item),
      player -> giveWeaponOrAmmo(player, Weapon.MACHINE_GUN),
      MACHINE_GUN_SPAWN_INTERVAL_TICKS,
      MACHINE_GUN_SPAWN_OFFSET_TICKS
  ),
  GAUNTLET(
      "<blue>перчатку</blue>",
      PowerupType.WEAPON,
      new ItemStack(Weapon.GAUNTLET.item),
      player -> giveWeaponOrAmmo(player, Weapon.GAUNTLET),
      MACHINE_GUN_SPAWN_INTERVAL_TICKS,
      MACHINE_GUN_SPAWN_OFFSET_TICKS
  ),
  ARMOR_SHARD(
      "<dark_purple>осколок брони</dark_purple>",
      PowerupType.ARMOR,
      new ItemStack(Material.SHIELD),
      player -> giveArmor(player, 5),
      ARMOR_SHARD_SPAWN_INTERVAL_TICKS,
      ARMOR_SHARD_SPAWN_OFFSET_TICKS
  ),
  LIGHT_ARMOR(
      "<yellow>броню</yellow>",
      PowerupType.ARMOR,
      new ItemStack(Material.GOLDEN_CHESTPLATE),
      player -> giveArmor(player, 50),
      LIGHT_ARMOR_SPAWN_INTERVAL_TICKS,
      LIGHT_ARMOR_SPAWN_OFFSET_TICKS
  );

  /**
   * Minimessage
   */
  private final String displayName;
  private final PowerupType type;
  private final ItemStack itemStack;
  private final Function<Player, Boolean> onPickup;
  private final long spawnInterval;
  private final int offset;

  private static boolean giveArmor(Player player, int amount) {
    if (player.getLevel() == MAX_ARMOR) {
      return false;
    }
    int armor = Math.min(player.getLevel() + amount, MAX_ARMOR);
    player.setLevel(armor);
    return true;
  }

  private static boolean giveWeaponOrAmmo(Player player, Weapon weapon) {
    if (weapon == null) {
      return false;
    }
    if (!hasWeapon(player, weapon)) {
      giveWeapon(player, weapon);
//            return true;
    }
    int amount = weapon.ammoPerShot * 3;
    if (weapon.ammo == Ammo.LIGHTNING) {
      amount = 100;
    }
    return giveAmmo(player, weapon.ammo, amount);
  }

  private static boolean genericHealth(Player player, double amount) {
    if (Math.abs(player.getHealth() - BASE_HEALTH) < .01 || BASE_HEALTH < player.getHealth()) {
      return false;
    }
    Optional.ofNullable(player.getAttribute(Attribute.GENERIC_MAX_HEALTH))
        .ifPresent(t -> t.setBaseValue(BASE_HEALTH));
    player.setHealth(Math.min(player.getHealth() + amount, BASE_HEALTH));
    return true;
  }

  private static void giveWeapon(Player player, Weapon weapon) {
    player.getInventory().setItem(weapon.slot, WeaponItemGenerator.generate(player, weapon));
  }

  private static boolean hasWeapon(Player player, Weapon weapon) {
    return player.getInventory().contains(weapon.item);
  }

  private static boolean giveAmmo(Player player, Ammo ammo, int amount) {
    var ammoValue = Ammo.getAmmoForPlayer(player, ammo);
    if (ammoValue >= ammo.max) {
      return false;
    }
    Ammo.setAmmoForPlayer(player, ammo, Math.min(ammoValue + amount, ammo.max));
    return true;
  }

  static ItemStack potionItem(Color color) {
    var is = new ItemStack(Material.POTION);
    var meta = ((PotionMeta) is.getItemMeta());
    meta.setColor(color);
    is.setItemMeta(meta);
    return is;
  }
}

