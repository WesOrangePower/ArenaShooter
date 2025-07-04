package agency.shitcoding.arena.models;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.events.listeners.DamageListener;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.models.door.Door;
import agency.shitcoding.arena.models.door.DoorTrigger;
import java.lang.reflect.InvocationTargetException;

import agency.shitcoding.arena.storage.framework.ConfigurationMappable;
import agency.shitcoding.arena.storage.framework.annotation.MappedField;
import lombok.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@NoArgsConstructor
public class Arena implements Cloneable, ConfigurationMappable {
  public static final Random spawnPointRandomizer = new Random();
  private boolean shifted = false;
  private String name;
  @MappedField private List<String> authors;

  @MappedField("lower_bound")
  private Location lowerBound;

  @MappedField("upper_bound")
  private Location upperBound;
  @MappedField("loot_points")
  private Set<LootPoint> lootPoints;
  @MappedField
  private Set<Portal> portals;
  @MappedField("wind_tunnels")
  private Set<WindTunnel> windTunnels;
  @MappedField
  private Set<Ramp> ramps;
  @MappedField
  private Set<Door> doors;
  @MappedField("door_triggers")
  private Set<DoorTrigger> doorTriggers;
  @MappedField("allow_host")
  private boolean allowHost;
  @MappedField
  private Set<String> tags;
  @MappedField("supported_rulesets")
  private Set<RuleSet> supportedRuleSets;


  private transient Set<LootPoint> weaponLootPoints;

  public Arena(
      String name,
      List<String> authors,
      Location lowerBound,
      Location upperBound,
      Set<LootPoint> lootPoints,
      Set<Portal> portals,
      Set<WindTunnel> windTunnels,
      Set<Ramp> ramps,
      Set<Door> doors,
      Set<DoorTrigger> doorTriggers,
      boolean allowHost,
      Set<String> tags,
      Set<RuleSet> supportedRuleSets) {
    this.name = name;
    this.authors = authors;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.lootPoints = lootPoints;
    this.portals = portals;
    this.windTunnels = windTunnels;
    this.ramps = ramps;
    this.doors = doors;
    this.doorTriggers = doorTriggers;
    this.allowHost = allowHost;
    this.tags = tags;
    this.supportedRuleSets = supportedRuleSets;
  }

  public boolean isInside(Location location) {
    return location.getX() >= lowerBound.getX()
        && location.getX() <= upperBound.getX()
        && location.getY() >= lowerBound.getY()
        && location.getY() <= upperBound.getY()
        && location.getZ() >= lowerBound.getZ()
        && location.getZ() <= upperBound.getZ();
  }

  public Set<LootPoint> getWeaponLootPoints() {
    if (weaponLootPoints == null) {
      weaponLootPoints =
          lootPoints.stream()
              .filter(
                  lootPoint ->
                      lootPoint.getType().getType() == PowerupType.WEAPON
                          || lootPoint.getType().getType() == PowerupType.SPAWN)
              .collect(Collectors.toSet());
    }
    return weaponLootPoints;
  }

  public LootPoint spawn(Player player, Game game, LootPointFilter filter) {
    LootPoint lootPoint = findLootPointToSpawn(filter, player);
    if (lootPoint == null) {
      ArenaShooter.getInstance()
          .getLogger()
          .log(
              Level.SEVERE,
              () -> "Failed to spawn player " + player.getName() + " in arena " + name);
      return null;
    }

    prepareAndSpawn(player, lootPoint, game);
    return lootPoint;
  }

  private void prepareAndSpawn(Player player, LootPoint lootPoint, Game game) {
    player.getInventory().clear();
    if (player.isDead()) {
      player.spigot().respawn();
    }
    if (player.getGameMode() != GameMode.ADVENTURE) {
      player.setGameMode(GameMode.ADVENTURE);
      player.setAllowFlight(true);
    }
    DamageListener.setBaseHealth(player);
    Optional.ofNullable(player.getAttribute(Attribute.MAX_HEALTH))
        .map(AttributeInstance::getBaseValue)
        .ifPresent(player::setHealth);
    if (!game.getRuleSet().getDefaultGameRules().doRespawn()
        && game.getDiedOnce().contains(player)
        && game.getGamestage() == GameStage.IN_PROGRESS) {
      player.setGameMode(GameMode.SPECTATOR);
    }
    if (player.getGameMode() != GameMode.SPECTATOR && game instanceof TeamGame teamGame) {
      teamGame
          .getTeamManager()
          .getTeam(player)
          .ifPresent(
              team -> {
                player.getInventory().setHelmet(team.getTeamMeta().getHelmet());
                player.getInventory().setChestplate(team.getTeamMeta().getChest());
                player.getInventory().setLeggings(team.getTeamMeta().getLeggings());
                player.getInventory().setBoots(team.getTeamMeta().getBoots());
              });
    }

    game.getRespawnInvulnerability().registerRespawn(player);

    player.teleport(lootPoint.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);

    GameRules gameRules = game.getRuleSet().getDefaultGameRules();
    List<Powerup> powerups = gameRules.spawnPowerups();
    Map<Ammo, Integer> ammoIntegerMap = gameRules.spawnAmmo();
    int spawnArmor = gameRules.spawnArmor();
    player.setLevel(spawnArmor);

    for (Powerup powerup : powerups) {
      powerup.getOnPickup().apply(player);
    }

    Ammo.setAmmoForPlayer(player, 0);
    for (Map.Entry<Ammo, Integer> entry : ammoIntegerMap.entrySet()) {
      Ammo ammo = entry.getKey();
      Integer amount = entry.getValue();
      Ammo.setAmmoForPlayer(player, ammo, amount);
    }
  }

  private LootPoint findLootPointToSpawn(LootPointFilter filter, Player player) {
    Set<LootPoint> weaponLootPoints =
        getWeaponLootPoints().stream()
            .filter(lp -> filter.filter(lp, player))
            .collect(Collectors.toSet());
    int size = weaponLootPoints.size();
    if (size == 0) {
      throw new IllegalStateException("No loot points to spawn player " + player.getName());
    }
    int item = spawnPointRandomizer.nextInt(size);
    int i = 0;
    for (LootPoint point : weaponLootPoints) {
      if (i == item) {
        return point;
      }
      i++;
    }
    return null;
  }

  @Override
  public Arena clone() {
    try {
      Arena arena = (Arena) super.clone();
      arena.name = name;
      arena.authors = new ArrayList<>(authors);
      arena.lowerBound = lowerBound.clone();
      arena.upperBound = upperBound.clone();
      arena.lootPoints = cloneSet(lootPoints);
      arena.portals = cloneSet(portals);
      arena.windTunnels = cloneSet(windTunnels);
      arena.ramps = cloneSet(ramps);
      arena.weaponLootPoints = null;
      arena.doors = cloneSet(doors);
      arena.doorTriggers = cloneSet(doorTriggers);
      arena.tags = shallowCloneSet(tags);
      arena.supportedRuleSets = shallowCloneSet(supportedRuleSets);
      return arena;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  @Contract("null -> null")
  private static <T extends Cloneable> @Nullable Set<T> cloneSet(@Nullable Set<T> set) {
    if (set == null) {
      return null;
    }
    Set<T> newSet = new HashSet<>();
    for (T cloneable : set) {
      try {
        var method = cloneable.getClass().getMethod("clone");
        method.setAccessible(true);
        //noinspection unchecked
        newSet.add((T) method.invoke(cloneable));
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new IllegalStateException("Failed to clone object", e);
      }
    }
    return newSet;
  }

  @Contract("null -> null")
  private static <T> Set<T> shallowCloneSet(Set<T> set) {
    return set == null ? null : new HashSet<>(set);
  }

  @Override
  public String getId() {
    return getName();
  }

  @Override
  public void setId(String id) {
    setName(id);
  }
}
