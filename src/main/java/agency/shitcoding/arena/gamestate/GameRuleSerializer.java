package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.CustomGameRules;
import agency.shitcoding.arena.models.CustomGameRulesBuilder;
import agency.shitcoding.arena.models.GameRules;
import com.google.gson.Gson;

public class GameRuleSerializer {

  private final Gson gson = new Gson();

  public String serialize(GameRules gameRules) {
    var customGameRules = CustomGameRulesBuilder.basedOn(gameRules).build();

    return gson.toJson(customGameRules);
  }

  public GameRules deserialize(String serialized) {
    return gson.fromJson(serialized, CustomGameRules.class);
  }
}
