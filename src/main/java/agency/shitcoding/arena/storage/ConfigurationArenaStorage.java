package agency.shitcoding.arena.storage;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.command.Conf;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.LootPoint;
import agency.shitcoding.arena.models.Powerup;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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

        if (lootPointsSection == null) {
            lootPointsSection = arenaSection.createSection(Conf.Arenas.lootPointsSection);
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
        return new Arena(name, lowerBound, upperBound, lootPoints);
    }

    private LootPoint parseLootPoint(String lootPointId, ConfigurationSection lootPointSection) {

        int id = Integer.parseInt(lootPointId.substring(2));
        Location location = lootPointSection.getLocation(Conf.Arenas.LootPoints.location);
        Powerup type = Powerup.valueOf(lootPointSection.getString(Conf.Arenas.LootPoints.type));

        return new LootPoint(id, location, type);
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
        setLootPointsSection(arenaSection, arena);
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
