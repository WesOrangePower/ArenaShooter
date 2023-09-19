package agency.shitcoding.doublejump.events.listeners;

import agency.shitcoding.doublejump.DoubleJump;
import agency.shitcoding.doublejump.GameplayConstants;
import agency.shitcoding.doublejump.events.GameDamageEvent;
import agency.shitcoding.doublejump.models.Weapon;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class DamageListener implements Listener {
    @EventHandler
    public void onDamage(GameDamageEvent event) {
        // if Has QuadDamage
        if (event.getDealer() != null) {
            event.getDealer().getActivePotionEffects().forEach(potionEffect -> {
                if (potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                    event.setDamage(event.getDamage() * GameplayConstants.QUAD_DAMAGE_MULTIPLIER);
                }
            });
        }

        // if Has Protection
        event.getVictim().getActivePotionEffects().forEach(potionEffect -> {
            if (potionEffect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                event.setDamage(event.getDamage() * GameplayConstants.PROTECTION_FACTOR);
            }
        });

        // player only zone
        if (event.getVictim() instanceof Player player) {
            // apply armor
            double damage = calculateDamage(player, event.getDamage());
            event.setDamage(damage);

            // gibbing
            if (player.getHealth() - event.getDamage() <= GameplayConstants.GIBBING_THRESHOLD) {
                gibbingSequence(player, event.getWeapon());
            }
        }
    }

    private double calculateDamage(Player victim, double damage) {
        var armor = victim.getExpToLevel();
        if (armor == 0) {
            return damage;
        }
        var armorDamage = damage * GameplayConstants.ARMOR_FACTOR;
        if (armorDamage > armor) {
            victim.setExp(0);
            return armor * GameplayConstants.ARMOR_FACTOR + (damage - armor);
        } else {
            victim.setLevel((int)(armor - armorDamage));
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
                .forEach(item -> Bukkit.getScheduler().runTaskLater(DoubleJump.getInstance(), item::remove, 20 * 3));

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
