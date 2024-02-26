package agency.shitcoding.arena.gamestate.team;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public abstract class ColoredArmorTeamMeta implements TeamMeta {

  private final Color color;

  public ColoredArmorTeamMeta(Color color) {
    this.color = color;
  }

  @Override
  public ItemStack getHelmet() {
    return setColor(new ItemStack(Material.LEATHER_HELMET));
  }

  @Override
  public ItemStack getChest() {
    return setColor(new ItemStack(Material.LEATHER_CHESTPLATE));
  }

  @Override
  public ItemStack getLeggings() {
    return setColor(new ItemStack(Material.LEATHER_LEGGINGS));
  }

  @Override
  public ItemStack getBoots() {
    return setColor(new ItemStack(Material.LEATHER_BOOTS));
  }

  private ItemStack setColor(ItemStack itemStack) {
    var leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
    leatherArmorMeta.setColor(color);
    itemStack.setItemMeta(leatherArmorMeta);
    return itemStack;
  }
}
