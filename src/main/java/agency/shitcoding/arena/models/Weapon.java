package agency.shitcoding.arena.models;

import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;

@RequiredArgsConstructor
public enum Weapon {
    GAUNTLET("Gauntlet", Color.BLUE, 0, Material.WOODEN_SWORD, Ammo.BULLETS, 0, 1),
    MACHINE_GUN("Machine Gun", Color.YELLOW, 1, Material.STONE_PICKAXE, Ammo.BULLETS, 1, 2),
    SHOTGUN("Shotgun", Color.ORANGE, 2, Material.WOODEN_PICKAXE, Ammo.SHELLS, 2, 16),
    ROCKET_LAUNCHER("Rocket Launcher", Color.RED, 3, Material.BOW, Ammo.ROCKETS, 1, 20),
    RAILGUN("Railgun", Color.AQUA, 5, Material.CROSSBOW, Ammo.CELLS, 20, 30);


    public final String name;
    public final Color color;
    public final int slot;
    public final Material item;
    public final Ammo ammo;
    public final int ammoPerShot;
    public final int cooldown;
}
