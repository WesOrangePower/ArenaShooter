package agency.shitcoding.arena.gamestate.team;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Team;

@Getter
@Setter
public abstract class PlayingTeam extends GameTeam {
    private Team scoreboardTeam;
    private int score;

    public int count() {
        return getPlayers().size();
    }
    public abstract String getDisplayName();
    public TextComponent getDisplayComponent() {
        return Component.text(getDisplayName()).color(getTextColor());
    }

    public ItemStack getHelmet() {
        return getLeatherArmorPiece(Material.LEATHER_HELMET);
    }
    public ItemStack getChest() {
        return getLeatherArmorPiece(Material.LEATHER_CHESTPLATE);
    }
    public ItemStack getLeggings() {
        return getLeatherArmorPiece(Material.LEATHER_LEGGINGS);
    }
    public ItemStack getBoots() {
        return getLeatherArmorPiece(Material.LEATHER_BOOTS);
    }

    public abstract Color getBukkitColor();
    public TextColor getTextColor() {
        return TextColor.color(getBukkitColor().asRGB());
    }

    private ItemStack getLeatherArmorPiece(Material material) {
        ItemStack itemStack = new ItemStack(material);
        itemStack.editMeta(meta -> ((LeatherArmorMeta) meta).setColor(getBukkitColor()));
        return itemStack;
    }
}