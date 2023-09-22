package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.events.AmmoUpdateEvent;
import agency.shitcoding.arena.events.GameNoAmmoEvent;
import agency.shitcoding.arena.events.GameShootEvent;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Optional;

public class InteractListener implements Listener {
    @EventHandler
    public void onShoot(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        Optional<Weapon> weaponOptional = Arrays.stream(Weapon.values())
                .filter(weapon -> weapon.item == itemInMainHand.getType())
                .findAny();
        Optional<Game> gameByPlayer = GameOrchestrator.getInstance()
                .getGameByPlayer(event.getPlayer());
        if (gameByPlayer.isEmpty() || weaponOptional.isEmpty())
            return;
        Game game = gameByPlayer.get();

        Weapon gun = weaponOptional.get();

        int ammoForPlayer = Ammo.getAmmoForPlayer(player, gun.ammo);
        if (gun.ammoPerShot > ammoForPlayer) {
            new GameNoAmmoEvent(game, player).fire();
            return;
        }
        if (player.hasCooldown(gun.item)) {
            return;
        }
        new AmmoUpdateEvent(player, -gun.ammoPerShot, gun.ammo).fire();
        new GameShootEvent(event, game, gun).fire();
    }
}
