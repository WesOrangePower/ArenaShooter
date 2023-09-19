package agency.shitcoding.arena;

import org.bukkit.potion.PotionEffectType;

public class GameplayConstants {
    public static final int QUAD_DAMAGE_DURATION = 30 * 20;
    public static final int QUAD_DAMAGE_MULTIPLIER = 4;
    public static final PotionEffectType QUAD_DAMAGE_POTION_EFFECT = PotionEffectType.INCREASE_DAMAGE;
    public static final int QUAD_DAMAGE_SPAWN_OFFSET_TICKS = 45 * 20;
    public static final int QUAD_DAMAGE_SPAWN_INTERVAL_TICKS = 120 * 20;

    public static final int PROTECTION_DURATION = 50 * 20;
    public static final double PROTECTION_FACTOR = .5;
    public static final int PROTECTION_SPAWN_OFFSET_TICKS = (60 + 45) * 20;
    public static final int PROTECTION_SPAWN_INTERVAL_TICKS = 120 * 20;

    public static final int AMMO_DROP_SPAWN_INTERVAL_TICKS = 20 * 20;
    public static final int AMMO_DROP_SPAWN_OFFSET_TICKS = 0;

    public static final int MACHINE_GUN_SPAWN_INTERVAL_TICKS = 30 * 20;
    public static final int MACHINE_GUN_SPAWN_OFFSET_TICKS = 0;
    public static final int SHOTGUN_SPAWN_INTERVAL_TICKS = 30 * 20;
    public static final int SHOTGUN_SPAWN_OFFSET_TICKS = 0;

    public static final int ROCKET_LAUNCHER_SPAWN_INTERVAL_TICKS = 60 * 20;
    public static final int ROCKET_LAUNCHER_SPAWN_OFFSET_TICKS = 0;

    public static final int RAILGUN_SPAWN_INTERVAL_TICKS = 60 * 20;
    public static final int RAILGUN_SPAWN_OFFSET_TICKS = 10 * 20;

    public static final int STIM_PACK_SPAWN_INTERVAL_TICKS = 30 * 20;
    public static final int STIM_PACK_SPAWN_OFFSET_TICKS = 0;
    public static final int MEDIKIT_SPAWN_INTERVAL_TICKS = 60 * 20;
    public static final int MEDIKIT_SPAWN_OFFSET_TICKS = 0;

    public static final int GIBBING_THRESHOLD = -7;
    public static final double ARMOR_FACTOR = .35;

    public static final double GAUNTLET_DAMAGE = 5;
    public static final double MACHINE_GUN_DAMAGE = 2;
    public static final double SHOTGUN_PELLET_DAMAGE = 2;
    public static final double ROCKET_DAMAGE = 10;
    public static final double RAILGUN_DAMAGE = 12;
}
