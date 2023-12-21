package agency.shitcoding.arena.models;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.team.PlayingTeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
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

@Getter
@Setter
public class Arena {
    public static final Random spawnPointRandomizer = new Random();
    public String name;
    public Location lowerBound;
    public Location upperBound;
    public Set<LootPoint> lootPoints;
    public Set<Portal> portals;
    public Set<Ramp> ramps;
    private Set<LootPoint> weaponLootPoints = null;
    private boolean allowHost;

    public Arena(String name, Location lowerBound, Location upperBound, Set<LootPoint> lootPoints, Set<Portal> portals, Set<Ramp> ramps, boolean allowHost) {
        this.name = name;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.lootPoints = lootPoints;
        this.portals = portals;
        this.ramps = ramps;
        this.allowHost = allowHost;
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
            player.setAllowFlight(true);
        }
        Optional.ofNullable(player.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                .map(AttributeInstance::getBaseValue)
                .ifPresent(player::setHealth);
        if (!game.getRuleSet().getGameRules().doRespawn() && game.getDiedOnce().contains(player) && game.getGamestage() == GameStage.IN_PROGRESS) {
            player.setGameMode(GameMode.SPECTATOR);
        }
        if (player.getGameMode() != GameMode.SPECTATOR && game instanceof TeamGame teamGame) {
            teamGame.getTeamManager().getTeam(player)
                    .filter(PlayingTeam.class::isInstance)
                    .map(PlayingTeam.class::cast)
                    .ifPresent(team -> {
                        player.getInventory().setHelmet(team.getHelmet());
                        player.getInventory().setChestplate(team.getChest());
                        player.getInventory().setLeggings(team.getLeggings());
                        player.getInventory().setBoots(team.getBoots());
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
}
