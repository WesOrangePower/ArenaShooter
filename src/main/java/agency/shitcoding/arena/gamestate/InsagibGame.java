package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class InsagibGame extends Game {
    public InsagibGame(Arena arena) {
        super(arena, RuleSet.INSTAGIB);
    }

    @Override
    protected @NotNull Set<LootPoint> preprocessLootPoints(Set<LootPoint> lootPoints) {
        return lootPoints.stream()
                .map(this::changeLootPointType)
                .collect(Collectors.toSet());
    }


    @Override
    protected void startGameStage2() {

    }

    private LootPoint changeLootPointType(LootPoint lootPoint) {
        PowerupType type = lootPoint.getType().getType();
        if (type == PowerupType.WEAPON) {
            return new LootPoint(lootPoint.getId(), lootPoint.getLocation(), Powerup.RAILGUN);
        }
        if (type == PowerupType.ARMOR || type == PowerupType.AMMO) {
            return new LootPoint(lootPoint.getId(), lootPoint.getLocation(), Powerup.CELL_BOX);
        }
        return lootPoint;
    }

}
