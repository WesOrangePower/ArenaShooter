package agency.shitcoding.arena.models;

import agency.shitcoding.arena.gamestate.DeathMatchGameFactory;
import agency.shitcoding.arena.gamestate.GameFactory;
import agency.shitcoding.arena.gamestate.LMSGameFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RuleSet{
    DM("Deathmatch", 16, 2, new DeathMatchGameFactory(), new DMGameRules()),
    LMS("Last man standing", 16, 2, new LMSGameFactory(), new LMSGameRules());

    private final String name;
    private final int maxPlayers;
    private final int minPlayers;
    private final GameFactory gameFactory;
    private final GameRules gameRules;
}

