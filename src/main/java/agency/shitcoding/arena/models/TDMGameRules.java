package agency.shitcoding.arena.models;

import agency.shitcoding.arena.gamestate.team.ETeam;
import org.bukkit.inventory.ItemStack;

public class TDMGameRules extends DMGameRules {

  @Override
  public boolean hasTeams() {
    return true;
  }

  @Override
  public ItemStack getMenuBaseItem() {
    return ETeam.BLUE.getTeamMeta().getHelmet();
  }
}
