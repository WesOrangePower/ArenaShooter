package agency.shitcoding.arena;

import org.bukkit.potion.PotionEffectType;

public final class GameplayConstants {

  public static final int GAME_END_TIMER_TICKS = 10 * 20;

  public static final int QUAD_DAMAGE_DURATION = 30 * 20;
  public static final int QUAD_DAMAGE_MULTIPLIER = 4;
  public static final PotionEffectType QUAD_DAMAGE_POTION_EFFECT = PotionEffectType.STRENGTH;
  public static final PotionEffectType PROTECTION_POTION_EFFECT = PotionEffectType.RESISTANCE;
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

  public static final int SHOTGUN_SPAWN_INTERVAL_TICKS = 10 * 20;
  public static final int SHOTGUN_SPAWN_OFFSET_TICKS = 0;

  public static final int BFG9K_SPAWN_INTERVAL_TICKS = 120 * 20;
  public static final int BFG9K_SPAWN_OFFSET_TICKS = 0;

  public static final int ROCKET_LAUNCHER_SPAWN_INTERVAL_TICKS = 15 * 20;
  public static final int ROCKET_LAUNCHER_SPAWN_OFFSET_TICKS = 0;

  public static final int LIGHTNING_GUN_SPAWN_INTERVAL_TICKS = 20 * 20;
  public static final int LIGHTNING_GUN_SPAWN_OFFSET_TICKS = 0;

  public static final int RAILGUN_SPAWN_INTERVAL_TICKS = 20 * 20;
  public static final int RAILGUN_SPAWN_OFFSET_TICKS = 10 * 20;


  public static final int PLASMA_GUN_SPAWN_INTERVAL_TICKS = 45 * 20;
  public static final int PLASMA_GUN_SPAWN_OFFSET_TICKS = 0;

  public static final int STIM_PACK_SPAWN_INTERVAL_TICKS = 30 * 20;
  public static final int STIM_PACK_SPAWN_OFFSET_TICKS = 0;
  public static final int MEDIKIT_SPAWN_INTERVAL_TICKS = 60 * 20;
  public static final int MEDIKIT_SPAWN_OFFSET_TICKS = 0;
  public static final int MEGA_HEALTH_SPAWN_INTERVAL_TICKS = 35 * 20;
  public static final int MEGA_HEALTH_SPAWN_OFFSET_TICKS = 0;
  public static final double MEGA_HEALTH_VALUE = 32d;
  public static final int MEGA_HEALTH_ARMOR = 50;

  public static final int ARMOR_SHARD_SPAWN_INTERVAL_TICKS = 20 * 20;
  public static final int ARMOR_SHARD_SPAWN_OFFSET_TICKS = 0;
  public static final int LIGHT_ARMOR_SPAWN_INTERVAL_TICKS = 25 * 20;
  public static final int LIGHT_ARMOR_SPAWN_OFFSET_TICKS = 0;

  public static final int GIBBING_THRESHOLD = -7;
  public static final double ARMOR_FACTOR = .3;
  public static final double ARMOR_DAMAGE_FACTOR = 2d;

  public static final double GAUNTLET_DAMAGE = 8.5d;
  public static final double MACHINE_GUN_DAMAGE = 1d;
  public static final double SHOTGUN_PELLET_DAMAGE = 2d;
  public static final double ROCKET_DAMAGE = 15d;
  public static final double LIGHTNING_DAMAGE = .35;
  public static final double RAILGUN_DAMAGE = 20d;
  public static final double BFG_DAMAGE = 45d;
  public static final double BFG_SPREAD_DAMAGE = 25d;
  public static final int MAX_ARMOR = 100;
  public static final int RESPAWN_INVULNERABILITY_DURATION = 20 * 2; // Milliseconds
  public static final boolean RESPAWN_INVULNERABILITY_DISABLE_ON_SHOOT = true;
  public static final double BASE_HEALTH = 20d;
  public static final boolean OVERDRIVE_HEALTH_TICK = true;
  public static final boolean OVERDRIVE_ARMOR_TICK = true;
  public static final long REMOVE_DEATH_DROP_AFTER_TICKS = 20 * 10L;
  public static final long FAST_WEAPON_SPAWN_INTERVAL_TICKS = 20 * 2L;

  private GameplayConstants() {
  }
}
