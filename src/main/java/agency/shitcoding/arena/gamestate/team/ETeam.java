package agency.shitcoding.arena.gamestate.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ETeam {
    RED(RedTeam.class),
    BLUE(BlueTeam.class),
    SPECTATOR(SpectatorTeam.class);

    private final Class<? extends GameTeam> teamClass;
}

