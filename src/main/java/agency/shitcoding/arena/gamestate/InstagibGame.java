package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class InstagibGame extends DeathMatchGame {

  public InstagibGame(Arena arena) {
    super(arena, RuleSet.INSTAGIB);
  }

  @Override
  protected @NotNull Set<LootPoint> preprocessLootPoints(Set<LootPoint> lootPoints) {
    return lootPoints.stream()
        .map(this::changeLootPointType)
        .collect(Collectors.toSet());
  }

  private LootPoint changeLootPointType(LootPoint lootPoint) {
    PowerupType type = lootPoint.getType().getType();
    if (type == PowerupType.WEAPON || lootPoint.getType() == Powerup.MEGA_HEALTH) {
      return new LootPoint(lootPoint.getId(), lootPoint.getLocation(), Powerup.RAILGUN);
    }
    if (type == PowerupType.ARMOR || type == PowerupType.AMMO ) {
      return new LootPoint(lootPoint.getId(), lootPoint.getLocation(), Powerup.CELL_BOX);
    }
    return lootPoint;
  }

}
