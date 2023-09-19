package agency.shitcoding.doublejump.models;

import agency.shitcoding.doublejump.GameplayConstants;
import agency.shitcoding.doublejump.WeaponItemGenerator;
import agency.shitcoding.doublejump.events.MajorBuffTracker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.function.Function;

import static agency.shitcoding.doublejump.GameplayConstants.*;

@RequiredArgsConstructor
@Getter
public enum Powerup {
    /**
     * Powerup
     * Heals the player for 2 hp (1 heart)
     */
    STIM_PACK(
            potionItem(Color.BLUE),
            player -> {
                var attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (attribute == null) {
                    return false;
                }
                var playerMaxHealth =attribute.getValue();
                if (Math.abs(player.getHealth() - playerMaxHealth) < .01) {
                    return false;
                }
                player.setHealth(Math.min(player.getHealth() + 5, playerMaxHealth));
                return true;
            },
            STIM_PACK_SPAWN_INTERVAL_TICKS,
            STIM_PACK_SPAWN_OFFSET_TICKS
    ),
    MEDICAL_KIT(
            potionItem(Color.RED),
            player -> {
                var attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (attribute == null) {
                    return false;
                }
                var playerMaxHealth = attribute.getValue();
                if (Math.abs(player.getHealth() - playerMaxHealth) < .01) {
                    return false;
                }
                player.setHealth(Math.min(player.getHealth() + 5, playerMaxHealth));
                return true;
            },
            MEDIKIT_SPAWN_INTERVAL_TICKS,
            MEDIKIT_SPAWN_OFFSET_TICKS
    ),
    ROCKET_BOX(
            new ItemStack(Material.RED_SHULKER_BOX),
            player -> giveAmmo(player, Ammo.ROCKETS, 5),
            AMMO_DROP_SPAWN_INTERVAL_TICKS,
            AMMO_DROP_SPAWN_OFFSET_TICKS
    ),
    BULLET_BOX(
            new ItemStack(Material.YELLOW_SHULKER_BOX),
            player -> giveAmmo(player, Ammo.BULLETS, 50),
            AMMO_DROP_SPAWN_INTERVAL_TICKS,
            AMMO_DROP_SPAWN_OFFSET_TICKS
    ),
    SHELL_BOX(
            new ItemStack(Material.ORANGE_SHULKER_BOX),
            player -> giveAmmo(player, Ammo.SHELLS, 10),
            AMMO_DROP_SPAWN_INTERVAL_TICKS,
            AMMO_DROP_SPAWN_OFFSET_TICKS
    ),
    CELL_BOX(
            new ItemStack(Material.CYAN_SHULKER_BOX),
            player -> giveAmmo(player, Ammo.CELLS, 50),
            AMMO_DROP_SPAWN_INTERVAL_TICKS,
            AMMO_DROP_SPAWN_OFFSET_TICKS
    ),
    QUAD_DAMAGE(
            new ItemStack(Material.NETHER_STAR),
            player -> {
                Integer quadDamageTicks = Objects.requireNonNullElse(
                        MajorBuffTracker.getQuadDamageTicks(),
                        QUAD_DAMAGE_DURATION
                );
                var effect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, quadDamageTicks, 4);
                player.addPotionEffect(effect);
                return true;
            },
            QUAD_DAMAGE_SPAWN_INTERVAL_TICKS,
            QUAD_DAMAGE_SPAWN_OFFSET_TICKS
    ),
    PROTECTION(
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            player -> {
                Integer protectionTicks = Objects.requireNonNullElse(
                        MajorBuffTracker.getProtectionTicks(),
                        GameplayConstants.PROTECTION_DURATION
                );
                PotionEffect effect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, protectionTicks, 1);
                player.addPotionEffect(effect);
                return true;
            },
            GameplayConstants.PROTECTION_SPAWN_INTERVAL_TICKS,
            GameplayConstants.PROTECTION_SPAWN_OFFSET_TICKS
    ),
    SHOTGUN(
            new ItemStack(Weapon.SHOTGUN.item),
            player -> giveWeaponOrAmmo(player, Weapon.SHOTGUN),
            SHOTGUN_SPAWN_INTERVAL_TICKS,
            SHOTGUN_SPAWN_OFFSET_TICKS
    ),
    ROCKET_LAUNCHER(
            new ItemStack(Weapon.ROCKET_LAUNCHER.item),
            player -> giveWeaponOrAmmo(player, Weapon.ROCKET_LAUNCHER),
            ROCKET_LAUNCHER_SPAWN_INTERVAL_TICKS,
            ROCKET_LAUNCHER_SPAWN_OFFSET_TICKS
    ),
    RAILGUN(
            new ItemStack(Weapon.RAILGUN.item),
            player -> giveWeaponOrAmmo(player, Weapon.RAILGUN),
            RAILGUN_SPAWN_INTERVAL_TICKS,
            RAILGUN_SPAWN_OFFSET_TICKS
    );

    private static boolean giveWeaponOrAmmo(Player player, Weapon weapon) {
        if (weapon == null) {
            return false;
        }
        if (!hasWeapon(player, weapon)) {
            giveWeapon(player, weapon);
            return true;
        }
        int amount = weapon.ammoPerShot * 3;
        return giveAmmo(player, weapon.ammo, amount);
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


    private final ItemStack itemStack;
    private final Function<Player, Boolean> onPickup;
    private final long spawnInterval;
    private final int offset;


    static ItemStack potionItem(Color color) {
        var is = new ItemStack(Material.POTION);
        var meta = ((PotionMeta) is.getItemMeta());
        meta.setColor(color);
        is.setItemMeta(meta);
        return is;
    }
}
