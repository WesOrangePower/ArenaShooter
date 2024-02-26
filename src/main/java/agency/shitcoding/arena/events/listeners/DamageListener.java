package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameFragEvent;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.gamestate.team.TeamManager;
import agency.shitcoding.arena.models.Weapon;
import java.util.Optional;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DamageListener implements Listener {

  @EventHandler
  public void onDamage(GameDamageEvent event) {
    @Nullable Player dealer = event.getDealer();
    LivingEntity victim = event.getVictim();

    if (dealer != null && victim instanceof Player victimPlayer) {
      Optional<Game> gameByPlayer = GameOrchestrator.getInstance().getGameByPlayer(victimPlayer);
      if (gameByPlayer.isPresent()) {
        Game game = gameByPlayer.get();
        // if Invulnerable
        if (game.getRespawnInvulnerability().hasInvulnerability(victimPlayer)) {
          event.setCancelled(true);
          return;
        }
        // if Teammate
        if (game instanceof TeamGame teamGame && dealer != victimPlayer) {
          TeamManager teamManager = teamGame.getTeamManager();
          if (teamManager.getTeam(dealer).equals(teamManager.getTeam(victimPlayer))) {
            event.setCancelled(true);
            return;
          }
        }
      }
    }

    // if Has QuadDamage
    if (dealer != null) {
      dealer.getActivePotionEffects().forEach(potionEffect -> {
        if (potionEffect.getType().equals(GameplayConstants.QUAD_DAMAGE_POTION_EFFECT)) {
          event.setDamage(event.getDamage() * GameplayConstants.QUAD_DAMAGE_MULTIPLIER);
        }
      });
      if (victim instanceof Player) {
        dealer.playSound(dealer, SoundConstants.HITSOUND, 1f, 1f);
      }
    }

    // if Has Protection
    victim.getActivePotionEffects().forEach(potionEffect -> {
      if (potionEffect.getType().equals(GameplayConstants.PROTECTION_POTION_EFFECT)) {
        event.setDamage(event.getDamage() * GameplayConstants.PROTECTION_FACTOR);
      }
    });
    victim.setNoDamageTicks(0);

    // player only zone
    if (victim instanceof Player playerVictim) {
      // apply armor
      double damage = calculateDamage(playerVictim, event.getDamage());
      event.setDamage(damage);

      if (playerVictim.getHealth() - damage < 0
          && playerVictim.getGameMode() == GameMode.ADVENTURE) {
        new GameFragEvent(playerVictim, dealer, event.getWeapon())
            .fire();
      }

      // gibbing
      if (playerVictim.getHealth() - event.getDamage() <= GameplayConstants.GIBBING_THRESHOLD) {
        gibbingSequence(playerVictim, event.getWeapon());
      }
    }

    applyDamage(victim, dealer, event.getDamage());
  }

  private void applyDamage(LivingEntity victim, Player dealer, double damage) {
    var entityDamageEvent = new EntityDamageEvent(victim, DamageCause.ENTITY_ATTACK, damage);
    victim.setLastDamageCause(entityDamageEvent);
    victim.setKiller(dealer);
    victim.setHealth(Math.max(victim.getHealth() - damage, 0));
  }

  @SuppressWarnings("deprecation")
  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getDamager().getType() == EntityType.PLAYER) {
      // Ignore all armor
      event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void rescaleHealthOnDamage(EntityDamageEvent event) {
    if (event.getEntityType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getEntity();
    var damage = event.getDamage();
    if (player.getHealth() - damage < GameplayConstants.BASE_HEALTH) {
      setBaseHealth(player);
    }
  }

  @EventHandler
  public void onFrag(GameFragEvent event) {
    if (event.getWeapon() == Weapon.GAUNTLET) {
      var killer = event.getKiller();
      if (killer != null) {
        killer.playSound(killer, SoundConstants.HUMILIATION, .5f, 1f);
      }
    }
  }

  public static void setBaseHealth(Player player) {
    var attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    if (attribute == null) {
      return;
    }
    if (Math.abs(attribute.getBaseValue() - GameplayConstants.BASE_HEALTH) < .01) {
      return;
    }
    attribute.setBaseValue(GameplayConstants.BASE_HEALTH);
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
      victim.setLevel(
          Math.max((int) (armor - armorDamage * GameplayConstants.ARMOR_DAMAGE_FACTOR), 0));
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
        .map(itemStack -> world.dropItem(eyeLoc, itemStack))
        .peek(item -> item.setVelocity(victim.getVelocity().multiply(0.5)))
        .peek(item -> item.setCanPlayerPickup(false))
        .forEach(item -> Bukkit.getScheduler()
            .runTaskLater(ArenaShooter.getInstance(), item::remove, 20 * 3));

    world.playSound(eyeLoc, Sound.ENTITY_PLAYER_BIG_FALL, 1f, 1);
    world.spawnParticle(Particle.BLOCK_CRACK, eyeLoc, 15, .5, .5, .5, .5,
        Material.REDSTONE_BLOCK.createBlockData());

    if (weapon == null) {
      return;
    }

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
