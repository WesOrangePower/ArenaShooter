package agency.shitcoding.arena.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CTFGameRules extends TDMGameRules implements GameRules {

  @Override
  public boolean hasTeams() {
    return true;
  }

  @Override
  public boolean allowJoinAfterStart() {
    return false;
  }

  @Override
  public ItemStack getMenuBaseItem() {
    return new ItemStack(Material.RED_WOOL);
  }
}
