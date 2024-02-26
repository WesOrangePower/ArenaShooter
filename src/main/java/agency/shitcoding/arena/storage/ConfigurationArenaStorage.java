package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.command.Conf;
import agency.shitcoding.arena.models.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class ConfigurationArenaStorage implements ArenaStorage {

  public static final String FILE_NAME = "arenas.yml";

  private final Configuration configuration;

  @Override
  public Collection<Arena> getArenas() {
    var allArenasSection = configuration.getConfigurationSection(Conf.arenasSection);
    if (allArenasSection == null) {
      allArenasSection = configuration.createSection(Conf.arenasSection);
    }
    return allArenasSection.getKeys(false).stream()
        .map(this::getArena)
        .toList();
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
    var lowerBound = arenaSection.getLocation(Conf.Arenas.lowerBound);
    var upperBound = arenaSection.getLocation(Conf.Arenas.upperBound);
    var lootPointsSection = arenaSection.getConfigurationSection(Conf.Arenas.lootPointsSection);
    var portalsSection = arenaSection.getConfigurationSection(Conf.Arenas.portalsSection);
    var rampsSection = arenaSection.getConfigurationSection(Conf.Arenas.rampsSection);
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

    return new Arena(name, lowerBound, upperBound, lootPoints, portals, ramps, allowHost);
  }


  private LootPoint parseLootPoint(String lootPointId, ConfigurationSection lootPointSection) {

    int id = Integer.parseInt(lootPointId.substring(2));
    Location location = lootPointSection.getLocation(Conf.Arenas.LootPoints.location);
    Powerup type = Powerup.valueOf(lootPointSection.getString(Conf.Arenas.LootPoints.type));

    return new LootPoint(id, location, type);
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
    var allArenasSection = configuration.getConfigurationSection(Conf.arenasSection);
    if (allArenasSection == null) {
      allArenasSection = configuration.createSection(Conf.arenasSection);
    }
    var arenaSection = allArenasSection.getConfigurationSection(arena.getName());
    if (arenaSection == null) {
      arenaSection = allArenasSection.createSection(arena.getName());
    }

    arenaSection.set(Conf.Arenas.lowerBound, arena.getLowerBound());
    arenaSection.set(Conf.Arenas.upperBound, arena.getUpperBound());
    arenaSection.set(Conf.Arenas.allowHost, arena.isAllowHost());
    setLootPointsSection(arenaSection, arena);
    setPortalSection(arenaSection, arena);
    setRampSection(arenaSection, arena);
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
      lootPointSection.set(Conf.Arenas.LootPoints.location, lootPoint.getLocation());
    }
    removeLeftoverLootPoints(allLootPointsSection, arena);
  }

  private void setPortalSection(ConfigurationSection configurationSection, Arena arena) {
    var allPortalsSection = configurationSection.getConfigurationSection(
        Conf.Arenas.portalsSection);
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

  private void save() {
    if (configuration instanceof YamlConfiguration yamlConfiguration) {
      try {
        yamlConfiguration.save(FILE_NAME);
      } catch (Exception e) {
        ArenaShooter.getInstance().getLogger().severe("Failed to save " + FILE_NAME);
      }
    }
  }

}
