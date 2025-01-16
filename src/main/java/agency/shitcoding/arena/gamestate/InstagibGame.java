package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.*;
import agency.shitcoding.arena.worlds.ArenaWorld;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class InstagibGame extends DeathMatchGame {

  public InstagibGame(ArenaWorld arena) {
    this(arena, RuleSet.INSTAGIB, RuleSet.INSTAGIB.getDefaultGameRules());
  }

  public InstagibGame(ArenaWorld arenaWorld, RuleSet ruleSet, GameRules gameRules) {
    super(arenaWorld, ruleSet, gameRules);
  }

  @Override
  protected @NotNull Set<LootPoint> preprocessLootPoints(Set<LootPoint> lootPoints) {
    return lootPoints.stream().map(this::changeLootPointType).collect(Collectors.toSet());
  }

  private LootPoint changeLootPointType(LootPoint lootPoint) {
    PowerupType type = lootPoint.getType().getType();
    if (type == PowerupType.WEAPON
        || lootPoint.getType() == Powerup.MEGA_HEALTH
        || type == PowerupType.MAJOR_BUFF) {
      return new LootPoint(
          lootPoint.getId(),
          lootPoint.getLocation(),
          lootPoint.isSpawnPoint(),
          Powerup.RAILGUN,
          lootPoint.getMarkers());
    }
    if (type == PowerupType.ARMOR || type == PowerupType.AMMO) {
      return new LootPoint(
          lootPoint.getId(),
          lootPoint.getLocation(),
          lootPoint.isSpawnPoint(),
          Powerup.CELL_BOX,
          lootPoint.getMarkers());
    }
    return lootPoint;
  }
}
