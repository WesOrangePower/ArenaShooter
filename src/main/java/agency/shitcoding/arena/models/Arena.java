package agency.shitcoding.arena.models;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
@Setter
public class Arena {
    public String name;
    public Location lowerBound;
    public Location upperBound;
    public Set<LootPoint> lootPoints;
    public static final Random spawnPointRandomizer = new Random();
    private Set<LootPoint> weaponLootPoints = null;

    public Arena(String name, Location lowerBound, Location upperBound, Set<LootPoint> lootPoints) {
        this.name = name;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.lootPoints = lootPoints;
    }

    public boolean isInside(Location location) {
        return location.getX() >= lowerBound.getX() && location.getX() <= upperBound.getX() &&
                location.getY() >= lowerBound.getY() && location.getY() <= upperBound.getY() &&
                location.getZ() >= lowerBound.getZ() && location.getZ() <= upperBound.getZ();
    }

    public Set<LootPoint> getWeaponLootPoints() {
        if (weaponLootPoints == null) {
            weaponLootPoints = lootPoints.stream()
                    .filter(lootPoint -> lootPoint.getType().getType() == PowerupType.WEAPON)
                    .collect(Collectors.toSet());
        }
        return weaponLootPoints;
    }

    public LootPoint spawn(Player player, Game game) {
        LootPoint lootPoint = findLootPointToSpawn();
        if (lootPoint == null) {
            ArenaShooter.getInstance().getLogger().log(Level.SEVERE, "Failed to spawn player " + player.getName() + " in arena " + name);
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
        }
        player.teleportAsync(lootPoint.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);

        GameRules gameRules = game.getRuleSet().getGameRules();
        List<Powerup> powerups = gameRules.spawnPowerups();
        Map<Ammo, Integer> ammoIntegerMap = gameRules.spawnAmmo();
        int spawnArmor = gameRules.spawnArmor();
        player.setLevel(spawnArmor);

        for (Powerup powerup : powerups) {
            powerup.getOnPickup().apply(player);
        }
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
        for(LootPoint point : lootPoints)
        {
            if (i == item) {
                return point;
            }
            i++;
        }
        return null;
    }
}
