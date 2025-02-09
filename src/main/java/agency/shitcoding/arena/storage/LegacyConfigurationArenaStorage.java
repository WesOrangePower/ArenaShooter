package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.command.Conf;
import agency.shitcoding.arena.models.*;
import agency.shitcoding.arena.models.door.Door;
import agency.shitcoding.arena.models.door.DoorTrigger;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/**
 * @deprecated Use {@link MappedConfigurationArenaStorage} instead.
 * This is used only for migration purposes.
*/
@SuppressWarnings("DeprecatedIsStillUsed")
@RequiredArgsConstructor
@Deprecated(since = "21.02.2025")
public class LegacyConfigurationArenaStorage implements ArenaStorage {

  public static final File FILE =
      new File(ArenaShooter.getInstance().getDataFolder(), "arenas.yml");

  private final Configuration configuration;

  @Override
  public Collection<Arena> getArenas() {
    var allArenasSection = configuration.getConfigurationSection(Conf.arenasSection);
    if (allArenasSection == null) {
      allArenasSection = configuration.createSection(Conf.arenasSection);
    }
    return allArenasSection.getKeys(false).stream().map(this::getArena).toList();
  }

  @Override
  public void deleteArena(Arena arena) {
    configuration.set(Conf.arenasSection + "." + arena.getName(), null);
  }

  @Override
  public @Nullable Arena getArena(String name) {
    var allArenasSection = configuration.getConfigurationSection(Conf.arenasSection);
    if (allArenasSection == null) {
      allArenasSection = configuration.createSection(Conf.arenasSection);
    }

    var arenaSection = allArenasSection.getConfigurationSection(name);
    if (arenaSection == null) {
      return null;
    }

    return parseArena(arenaSection);
  }

  private Arena parseArena(ConfigurationSection arenaSection) {
    String name = arenaSection.getName();
    var authors = arenaSection.getStringList(Conf.Arenas.authors);
    var lowerBound = arenaSection.getLocation(Conf.Arenas.lowerBound);
    var upperBound = arenaSection.getLocation(Conf.Arenas.upperBound);
    var lootPointsSection = arenaSection.getConfigurationSection(Conf.Arenas.lootPointsSection);
    var portalsSection = arenaSection.getConfigurationSection(Conf.Arenas.portalsSection);
    var rampsSection = arenaSection.getConfigurationSection(Conf.Arenas.rampsSection);
    var doorsSection = arenaSection.getConfigurationSection(Conf.Arenas.doorsSection);
    var doorTriggersSection = arenaSection.getConfigurationSection(Conf.Arenas.doorTriggersSection);
    var tags = new HashSet<>(arenaSection.getStringList(Conf.Arenas.tags));
    var supportedRuleSets =
        arenaSection.getStringList(Conf.Arenas.supportedRuleSets).stream()
            .map(RuleSet::valueOf)
            .collect(Collectors.toSet());
    var allowHost = arenaSection.getBoolean(Conf.Arenas.allowHost, true);

    if (lootPointsSection == null) {
      lootPointsSection = arenaSection.createSection(Conf.Arenas.lootPointsSection);
    }
    if (portalsSection == null) {
      portalsSection = arenaSection.createSection(Conf.Arenas.portalsSection);
    }
    if (rampsSection == null) {
      rampsSection = arenaSection.createSection(Conf.Arenas.rampsSection);
    }
    if (doorsSection == null) {
      doorsSection = arenaSection.createSection(Conf.Arenas.doorsSection);
    }
    if (doorTriggersSection == null) {
      doorTriggersSection = arenaSection.createSection(Conf.Arenas.doorTriggersSection);
    }

    Set<LootPoint> lootPoints = new HashSet<>();
    for (String id : lootPointsSection.getKeys(false)) {
      var configurationSection = lootPointsSection.getConfigurationSection(id);
      if (configurationSection == null) {
        continue;
      }
      LootPoint lootPoint = parseLootPoint(id, configurationSection);
      lootPoints.add(lootPoint);
    }

    Set<Portal> portals = new HashSet<>();
    for (String id : portalsSection.getKeys(false)) {
      var configurationSection = portalsSection.getConfigurationSection(id);
      if (configurationSection == null) {
        continue;
      }
      Portal portal = parsePortal(id, configurationSection);
      portals.add(portal);
    }

    Set<Ramp> ramps = new HashSet<>();
    for (String id : rampsSection.getKeys(false)) {
      var configurationSection = rampsSection.getConfigurationSection(id);
      if (configurationSection == null) {
        continue;
      }
      Ramp ramp = parseRamp(id, configurationSection);
      ramps.add(ramp);
    }

    Set<Door> doors = new HashSet<>();
    for (String id : doorsSection.getKeys(false)) {
      var configurationSection = doorsSection.getConfigurationSection(id);
      if (configurationSection == null) {
        continue;
      }
      Door door = parseDoor(id, configurationSection);
      doors.add(door);
    }

    Set<DoorTrigger> doorTriggers = new HashSet<>();
    for (String id : doorTriggersSection.getKeys(false)) {
      var configurationSection = doorTriggersSection.getConfigurationSection(id);
      if (configurationSection == null) {
        continue;
      }
      DoorTrigger doorTrigger = parseDoorTrigger(id, configurationSection);
      doorTriggers.add(doorTrigger);
    }

    return new Arena(
        name,
        authors,
        lowerBound,
        upperBound,
        lootPoints,
        portals,
        ramps,
        doors,
        doorTriggers,
        allowHost,
        tags,
        supportedRuleSets);
  }

  private Door parseDoor(String id, ConfigurationSection configurationSection) {
    Location edge1 = configurationSection.getLocation(Conf.Arenas.Doors.firstLocation);
    Location edge2 = configurationSection.getLocation(Conf.Arenas.Doors.secondLocation);
    Location destinationCenter =
        configurationSection.getLocation(Conf.Arenas.Doors.destinationCenter);
    int doorType = configurationSection.getInt(Conf.Arenas.Doors.doorType);
    int animationTime = configurationSection.getInt(Conf.Arenas.Doors.animationTime);
    int closeAfterTicks = configurationSection.getInt(Conf.Arenas.Doors.closeAfterTicks);
    boolean replaceAir = configurationSection.getBoolean(Conf.Arenas.Doors.replaceAir);

    if (edge1 == null || edge2 == null || destinationCenter == null) {
      throw new RuntimeException("Failed to parse door");
    }
    return new Door(
        id, doorType, animationTime, closeAfterTicks, replaceAir, edge1, edge2, destinationCenter);
  }

  private DoorTrigger parseDoorTrigger(String id, ConfigurationSection configurationSection) {
    List<String> doorIds = configurationSection.getStringList(Conf.Arenas.DoorTriggers.doorIds);
    int triggerType = configurationSection.getInt(Conf.Arenas.DoorTriggers.triggerType);
    Location location = configurationSection.getLocation(Conf.Arenas.DoorTriggers.location);

    if (location == null) {
      throw new RuntimeException("Failed to parse door trigger");
    }

    return new DoorTrigger(id, doorIds, triggerType, location);
  }

  private LootPoint parseLootPoint(String lootPointId, ConfigurationSection lootPointSection) {
    Location location = lootPointSection.getLocation(Conf.Arenas.LootPoints.location);
    boolean isSpawnPoint = lootPointSection.getBoolean(Conf.Arenas.LootPoints.isSpawnPoint, true);
    Powerup type = Powerup.valueOf(lootPointSection.getString(Conf.Arenas.LootPoints.type));
    int markers = lootPointSection.getInt(Conf.Arenas.LootPoints.markers, 0);

    return new LootPoint(lootPointId, location, isSpawnPoint, type, markers);
  }

  private Portal parsePortal(String id, ConfigurationSection portalSection) {
    Location firstLocation = portalSection.getLocation(Conf.Arenas.Portals.firstLocation);
    Location secondLocation = portalSection.getLocation(Conf.Arenas.Portals.secondLocation);
    Location targetLocation = portalSection.getLocation(Conf.Arenas.Portals.targetLocation);

    if (firstLocation == null || secondLocation == null || targetLocation == null) {
      throw new RuntimeException("Failed to parse portal");
    }

    return new Portal(id, firstLocation, secondLocation, targetLocation);
  }

  private Ramp parseRamp(String rampId, ConfigurationSection rampSection) {
    Location firstLocation = rampSection.getLocation(Conf.Arenas.Ramps.firstLocation);
    Location secondLocation = rampSection.getLocation(Conf.Arenas.Ramps.secondLocation);
    boolean multiply = rampSection.getBoolean(Conf.Arenas.Ramps.multiply);
    Vector vector = rampSection.getVector(Conf.Arenas.Ramps.vector);

    if (firstLocation == null || secondLocation == null || vector == null) {
      throw new RuntimeException("Failed to parse ramp");
    }

    return new Ramp(rampId, firstLocation, secondLocation, multiply, vector);
  }

  @Override
  public void storeArena(Arena arena) {
    if (arena.isShifted()) throw new IllegalArgumentException("Arena is shifted");
    var allArenasSection = configuration.getConfigurationSection(Conf.arenasSection);
    if (allArenasSection == null) {
      allArenasSection = configuration.createSection(Conf.arenasSection);
    }
    var arenaSection = allArenasSection.getConfigurationSection(arena.getName());
    if (arenaSection == null) {
      arenaSection = allArenasSection.createSection(arena.getName());
    }

    arenaSection.set(Conf.Arenas.authors, arena.getAuthors());
    arenaSection.set(Conf.Arenas.lowerBound, arena.getLowerBound());
    arenaSection.set(Conf.Arenas.upperBound, arena.getUpperBound());
    arenaSection.set(Conf.Arenas.allowHost, arena.isAllowHost());
    setLootPointsSection(arenaSection, arena);
    setPortalSection(arenaSection, arena);
    setRampSection(arenaSection, arena);
    setDoorSection(arenaSection, arena);
    setDoorTriggerSection(arenaSection, arena);
    arenaSection.set(Conf.Arenas.tags, arena.getTags());
    arenaSection.set(
        Conf.Arenas.supportedRuleSets,
        arena.getSupportedRuleSets().stream().map(Enum::name).toList());
    save();
  }

  private void setLootPointsSection(ConfigurationSection arenaSection, Arena arena) {
    var allLootPointsSection = arenaSection.getConfigurationSection(Conf.Arenas.lootPointsSection);
    if (allLootPointsSection == null) {
      allLootPointsSection = arenaSection.createSection(Conf.Arenas.lootPointsSection);
    }

    for (LootPoint lootPoint : arena.getLootPoints()) {
      var lootPointSection = allLootPointsSection.getConfigurationSection("LP" + lootPoint.getId());
      if (lootPointSection == null) {
        lootPointSection = allLootPointsSection.createSection("LP" + lootPoint.getId());
      }
      lootPointSection.set(Conf.Arenas.LootPoints.type, lootPoint.getType().name());
      lootPointSection.set(Conf.Arenas.LootPoints.isSpawnPoint, lootPoint.isSpawnPoint());
      lootPointSection.set(Conf.Arenas.LootPoints.location, lootPoint.getLocation());
      lootPointSection.set(Conf.Arenas.LootPoints.markers, lootPoint.getMarkers());
    }
    removeLeftoverLootPoints(allLootPointsSection, arena);
  }

  private void setPortalSection(ConfigurationSection configurationSection, Arena arena) {
    var allPortalsSection =
        configurationSection.getConfigurationSection(Conf.Arenas.portalsSection);
    if (allPortalsSection == null) {
      allPortalsSection = configurationSection.createSection(Conf.Arenas.portalsSection);
    }

    for (Portal portal : arena.getPortals()) {
      var portalSection = allPortalsSection.getConfigurationSection(portal.getId());
      if (portalSection == null) {
        portalSection = allPortalsSection.createSection(portal.getId());
      }
      portalSection.set(Conf.Arenas.Portals.firstLocation, portal.getFirstLocation());
      portalSection.set(Conf.Arenas.Portals.secondLocation, portal.getSecondLocation());
      portalSection.set(Conf.Arenas.Portals.targetLocation, portal.getTargetLocation());
    }
    removeLeftoverPortals(allPortalsSection, arena);
  }

  private void setRampSection(ConfigurationSection section, Arena arena) {
    var allRampsSection = section.getConfigurationSection(Conf.Arenas.rampsSection);
    if (allRampsSection == null) {
      allRampsSection = section.createSection(Conf.Arenas.rampsSection);
    }

    for (Ramp ramp : arena.getRamps()) {
      var rampSection = allRampsSection.getConfigurationSection(ramp.getId());
      if (rampSection == null) {
        rampSection = allRampsSection.createSection(ramp.getId());
      }
      rampSection.set(Conf.Arenas.Ramps.firstLocation, ramp.getFirstLocation());
      rampSection.set(Conf.Arenas.Ramps.secondLocation, ramp.getSecondLocation());
      rampSection.set(Conf.Arenas.Ramps.multiply, ramp.isMultiply());
      rampSection.set(Conf.Arenas.Ramps.vector, ramp.getVector());
    }
    removeLeftoverRamps(allRampsSection, arena);
  }

  private void setDoorSection(ConfigurationSection section, Arena arena) {
    var allDoorsSection = section.getConfigurationSection(Conf.Arenas.doorsSection);
    if (allDoorsSection == null) {
      allDoorsSection = section.createSection(Conf.Arenas.doorsSection);
    }

    for (Door door : arena.getDoors()) {
      var doorSection = allDoorsSection.getConfigurationSection(door.getId());
      if (doorSection == null) {
        doorSection = allDoorsSection.createSection(door.getId());
      }
      doorSection.set(Conf.Arenas.Doors.firstLocation, door.getEdge1());
      doorSection.set(Conf.Arenas.Doors.secondLocation, door.getEdge2());
      doorSection.set(Conf.Arenas.Doors.destinationCenter, door.getDestinationCenter());
      doorSection.set(Conf.Arenas.Doors.doorType, door.getDoorType());
      doorSection.set(Conf.Arenas.Doors.animationTime, door.getAnimationTime());
      doorSection.set(Conf.Arenas.Doors.closeAfterTicks, door.getCloseAfterTicks());
      doorSection.set(Conf.Arenas.Doors.replaceAir, door.isReplaceAir());
    }
    removeLeftoverDoors(allDoorsSection, arena);
  }

  private void setDoorTriggerSection(ConfigurationSection section, Arena arena) {
    var allDoorTriggersSection = section.getConfigurationSection(Conf.Arenas.doorTriggersSection);
    if (allDoorTriggersSection == null) {
      allDoorTriggersSection = section.createSection(Conf.Arenas.doorTriggersSection);
    }

    for (DoorTrigger doorTrigger : arena.getDoorTriggers()) {
      var doorTriggerSection = allDoorTriggersSection.getConfigurationSection(doorTrigger.getId());
      if (doorTriggerSection == null) {
        doorTriggerSection = allDoorTriggersSection.createSection(doorTrigger.getId());
      }
      doorTriggerSection.set(Conf.Arenas.DoorTriggers.triggerType, doorTrigger.getTriggerType());
      doorTriggerSection.set(Conf.Arenas.DoorTriggers.location, doorTrigger.getLocation());
      doorTriggerSection.set(Conf.Arenas.DoorTriggers.doorIds, doorTrigger.getDoorIds());
    }
    removeLeftoverDoorTriggers(allDoorTriggersSection, arena);
  }

  private void removeLeftoverLootPoints(ConfigurationSection allLootPointsSection, Arena arena) {
    for (String key : allLootPointsSection.getKeys(false)) {
      if (arena.getLootPoints().stream().noneMatch(lp -> ("LP" + lp.getId()).equals(key))) {
        allLootPointsSection.set(key, null);
      }
    }
  }

  private void removeLeftoverPortals(ConfigurationSection allPortalsSection, Arena arena) {
    for (String key : allPortalsSection.getKeys(false)) {
      if (arena.getPortals().stream().noneMatch(p -> p.getId().equals(key))) {
        allPortalsSection.set(key, null);
      }
    }
  }

  private void removeLeftoverRamps(ConfigurationSection allRampsSection, Arena arena) {
    for (String key : allRampsSection.getKeys(false)) {
      if (arena.getRamps().stream().noneMatch(r -> r.getId().equals(key))) {
        allRampsSection.set(key, null);
      }
    }
  }

  private void removeLeftoverDoors(ConfigurationSection allDoorsSection, Arena arena) {
    for (String key : allDoorsSection.getKeys(false)) {
      if (arena.getDoors().stream().noneMatch(d -> d.getId().equals(key))) {
        allDoorsSection.set(key, null);
      }
    }
  }

  private void removeLeftoverDoorTriggers(
      ConfigurationSection allDoorTriggersSection, Arena arena) {
    for (String key : allDoorTriggersSection.getKeys(false)) {
      if (arena.getDoorTriggers().stream().noneMatch(dt -> dt.getId().equals(key))) {
        allDoorTriggersSection.set(key, null);
      }
    }
  }

  private void save() {
    if (configuration instanceof YamlConfiguration yamlConfiguration) {
      try {
        yamlConfiguration.save(FILE);
      } catch (Exception e) {
        ArenaShooter.getInstance().getLogger().severe("Failed to save " + FILE);
      }
    }
  }
}
