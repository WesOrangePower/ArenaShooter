package agency.shitcoding.arena.models;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.events.listeners.DamageListener;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.models.door.Door;
import agency.shitcoding.arena.models.door.DoorTrigger;
import java.lang.reflect.InvocationTargetException;
import lombok.Getter;
import lombok.Setter;
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
public class Arena implements Cloneable {
  public static final Random spawnPointRandomizer = new Random();
  private boolean shifted = false;
  private String name;
  private List<String> authors;
  private Location lowerBound;
  private Location upperBound;
  private Set<LootPoint> lootPoints;
  private Set<Portal> portals;
  private Set<Ramp> ramps;
  private Set<LootPoint> weaponLootPoints;
  private Set<Door> doors;
  private Set<DoorTrigger> doorTriggers;
  private boolean allowHost;

  public Arena(
      String name,
      List<String> authors,
      Location lowerBound,
      Location upperBound,
      Set<LootPoint> lootPoints,
      Set<Portal> portals,
      Set<Ramp> ramps,
      Set<Door> doors,
      Set<DoorTrigger> doorTriggers,
      boolean allowHost) {
    this.name = name;
    this.authors = authors;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.lootPoints = lootPoints;
    this.portals = portals;
    this.ramps = ramps;
    this.doors = doors;
    this.doorTriggers = doorTriggers;
    this.allowHost = allowHost;
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
              .filter(lootPoint -> lootPoint.getType().getType() == PowerupType.WEAPON)
              .collect(Collectors.toSet());
    }
    return weaponLootPoints;
  }

  public LootPoint spawn(Player player, Game game) {
    LootPoint lootPoint = findLootPointToSpawn();
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
    Optional.ofNullable(player.getAttribute(Attribute.GENERIC_MAX_HEALTH))
        .map(AttributeInstance::getBaseValue)
        .ifPresent(player::setHealth);
    if (!game.getRuleSet().getGameRules().doRespawn()
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

    GameRules gameRules = game.getRuleSet().getGameRules();
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

  private LootPoint findLootPointToSpawn() {
    Set<LootPoint> weaponLootPoints = getWeaponLootPoints();
    int size = weaponLootPoints.size();
    int item = spawnPointRandomizer.nextInt(size);
    int i = 0;
    for (LootPoint point : lootPoints) {
      if (i == item) {
        return point;
      }
      i++;
    }
    return null;
  }

  public Arena copy() {
    var newLootPoints = new HashSet<LootPoint>();
    for (LootPoint lootPoint : lootPoints) {
      newLootPoints.add(lootPoint.clone());
    }

    return new Arena(
        name,
        authors,
        lowerBound.clone(),
        upperBound.clone(),
        newLootPoints,
        portals,
        ramps,
        doors,
        doorTriggers,
        allowHost);
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
      arena.ramps = cloneSet(ramps);
      arena.weaponLootPoints = cloneSet(weaponLootPoints);
      arena.doors = cloneSet(doors);
      arena.doorTriggers = cloneSet(doorTriggers);
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
}
