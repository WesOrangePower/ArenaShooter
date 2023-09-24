package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.models.Weapon;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(GameDamageEvent event) {
        // if Has QuadDamage
        Player dealer = event.getDealer();
        if (dealer != null) {
            dealer.getActivePotionEffects().forEach(potionEffect -> {
                if (potionEffect.getType().equals(GameplayConstants.QUAD_DAMAGE_POTION_EFFECT)) {
                    event.setDamage(event.getDamage() * GameplayConstants.QUAD_DAMAGE_MULTIPLIER);
                }
            });
        }

        // if Has Protection
        event.getVictim().getActivePotionEffects().forEach(potionEffect -> {
            if (potionEffect.getType().equals(GameplayConstants.PROTECTION_POTION_EFFECT)) {
                event.setDamage(event.getDamage() * GameplayConstants.PROTECTION_FACTOR);
            }
        });
        event.getVictim().setNoDamageTicks(0);

        // player only zone
        if (event.getVictim() instanceof Player player) {
            // apply armor
            double damage = calculateDamage(player, event.getDamage());
            event.setDamage(damage);

            // gibbing
            if (player.getHealth() - event.getDamage() <= GameplayConstants.GIBBING_THRESHOLD) {
                gibbingSequence(player, event.getWeapon());
            }

            player.damage(event.getDamage(), dealer);
            return;
        }

        event.getVictim().damage(event.getDamage(), dealer);
    }

    private double calculateDamage(Player victim, double damage) {
        var armor = victim.getLevel();
        if (armor <= 0) {
            armor = 0;
        }
        if (armor == 0) {
            return damage;
        }
        var armorDamage = damage * GameplayConstants.ARMOR_FACTOR;
        if (armorDamage > armor) {
            victim.setLevel(0);
            return armor * GameplayConstants.ARMOR_FACTOR + (damage - armor);
        } else {
            victim.setLevel(Math.max((int)(armor - armorDamage * GameplayConstants.ARMOR_DAMAGE_FACTOR), 0));
            return damage * GameplayConstants.ARMOR_FACTOR;
        }
    }

    private void gibbingSequence(@NotNull Player victim, @Nullable Weapon weapon) {
        Location eyeLoc = victim.getEyeLocation();
        World world = eyeLoc.getWorld();
        ItemStack head = new ItemStack(Material.PLAYER_HEAD); // HDB perhaps
        ItemStack bone = new ItemStack(Material.BONE);
        ItemStack meat = new ItemStack(Material.ROTTEN_FLESH);
        Stream.of(head, bone, meat)
                .map(itemStack -> world.dropItem(eyeLoc, itemStack) )
                .peek(item -> item.setVelocity(victim.getVelocity().multiply(0.5)))
                .peek(item -> item.setCanPlayerPickup(false))
                .forEach(item -> Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), item::remove, 20 * 3));

        world.playSound(eyeLoc, Sound.ENTITY_PLAYER_BIG_FALL, 1f, 1);
        world.spawnParticle(Particle.BLOCK_CRACK, eyeLoc, 15, .5,.5,.5, .5, Material.REDSTONE_BLOCK.createBlockData());

        if (weapon == null) return;

        switch (weapon) {
            case ROCKET_LAUNCHER -> {
                world.spawnParticle(Particle.EXPLOSION_HUGE, eyeLoc, 1);
                world.playSound(eyeLoc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 2);
            }
            case RAILGUN -> {
                world.spawnParticle(Particle.ELECTRIC_SPARK, eyeLoc, 10, 0, 0, 0, .8);
                world.playSound(eyeLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, .4f, 2);
            }
            case GAUNTLET -> {
                world.spawnParticle(Particle.VILLAGER_ANGRY, eyeLoc, 10, .5, .5, .5, .8);
                world.playSound(eyeLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, .4f, 2);
            }
        }
        }
}
