package agency.shitcoding.arena.models;

import agency.shitcoding.arena.gamestate.DeathMatchGameFactory;
import agency.shitcoding.arena.gamestate.GameFactory;
import agency.shitcoding.arena.gamestate.InstagibGameFactory;
import agency.shitcoding.arena.gamestate.LMSGameFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RuleSet {
    DM("Deathmatch", 10 * 60, 16, 2, new DeathMatchGameFactory(), new DMGameRules()),
    LMS("Last man standing", 7 * 60, 16, 2, new LMSGameFactory(), new LMSGameRules()),
    INSTAGIB("Instagib", 8 * 60, 16, 3, new InstagibGameFactory(), new InstagibGameRules());

    private final String name;
    private final int gameLenSeconds;
    private final int maxPlayers;
    private final int minPlayers;
    private final GameFactory gameFactory;
    private final GameRules gameRules;
}

