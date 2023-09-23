package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.*;

import java.util.stream.Collectors;

public class InsagibGame extends Game {
    public InsagibGame(Arena arena) {
        super(arena, RuleSet.INSTAGIB);
    }

    @Override
    protected void startGameStage2() {
        getArena().setLootPoints(
        getArena().getLootPoints().stream()
                .map(lp -> {
                    PowerupType type = lp.getType().getType();
                    if (type == PowerupType.WEAPON) {
                        return new LootPoint(lp.getId(), lp.getLocation(), Powerup.RAILGUN);
                    }
                    if (type == PowerupType.ARMOR || type == PowerupType.AMMO) {
                        return new LootPoint(lp.getId(), lp.getLocation(), Powerup.CELL_BOX);
                    }
                    return lp;
                })
                .collect(Collectors.toSet())
        );
    }
}
