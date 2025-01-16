package agency.shitcoding.arena.events.listeners;

import static org.bukkit.Sound.ENTITY_PLAYER_HURT;

import agency.shitcoding.arena.AnnouncerConstant;
import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.GameplayConstants;
import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameFragEvent;
import agency.shitcoding.arena.events.GameStreakUpdateEvent;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.gamestate.team.TeamManager;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DamageListener implements Listener {

  private Random rng;

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

  @EventHandler
  public void onDamage(GameDamageEvent event) {
    @Nullable Player dealer = event.getDealer();
    LivingEntity victim = event.getVictim();

    if (dealer != null && victim instanceof Player victimPlayer) {
      if (victimPlayer.getGameMode() == GameMode.SPECTATOR) {
        return;
      }

      Optional<Game> gameByPlayer = GameOrchestrator.getInstance().getGameByPlayer(victimPlayer);
      if (gameByPlayer.isPresent()) {
        Game game = gameByPlayer.get();
        // if Game is over
        if (game.getGamestage() == GameStage.FINISHED) {
          event.setCancelled(true);
          return;
        }
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
      dealer
          .getActivePotionEffects()
          .forEach(
              potionEffect -> {
                if (potionEffect.getType().equals(GameplayConstants.QUAD_DAMAGE_POTION_EFFECT)) {
                  event.setDamage(event.getDamage() * GameplayConstants.QUAD_DAMAGE_MULTIPLIER);
                }
              });
      if (victim instanceof Player) {
        dealer.playSound(dealer, SoundConstants.HITSOUND, 1f, 1f);
      }
    }

    // if Has Protection
    victim
        .getActivePotionEffects()
        .forEach(
            potionEffect -> {
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

      // fragging
      var remainingHealth = playerVictim.getHealth() - damage;
      if (remainingHealth <= 0d && playerVictim.getGameMode() == GameMode.ADVENTURE) {
        var isGibbed = remainingHealth <= GameplayConstants.GIBBING_THRESHOLD;
        new GameFragEvent(playerVictim, dealer, event.getWeapon(), isGibbed).fire();
      }
    }

    applyDamage(victim, dealer, event.getDamage());
  }

  private void applyDamage(LivingEntity victim, Player dealer, double damage) {
    if (victim instanceof Player victimPlayer) {
      if (victimPlayer.getGameMode() != GameMode.ADVENTURE) {
        return;
      }
      Optional.ofNullable(ArenaShooter.getInstance().getWorldBorderApi())
          .ifPresent(api -> api.sendRedScreen(victimPlayer, (long) damage * 4));
    }

    /* Set last dealer as last damage source for the victim */

    var damageSourceBuilder = DamageSource.builder(DamageType.PLAYER_ATTACK);
    if (dealer != null) {
      damageSourceBuilder = damageSourceBuilder.withCausingEntity(dealer).withDirectEntity(dealer);
    }

    var damageSource = damageSourceBuilder.withDamageLocation(victim.getLocation()).build();
    var entityDamageEvent =
        new EntityDamageEvent(victim, DamageCause.ENTITY_ATTACK, damageSource, damage);

    Bukkit.getPluginManager().callEvent(entityDamageEvent);

    victim.setKiller(dealer);
    victim.playSound(
        Sound.sound().type(ENTITY_PLAYER_HURT).source(Source.VOICE).volume(1f).build());
    victim.setHealth(Math.max(victim.getHealth() - damage, 0));
  }

  @SuppressWarnings("deprecation")
  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getDamager().getType() == EntityType.PLAYER
        && event.getEntityType() == EntityType.PLAYER) {
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
  public void onFragIncreaseStreak(GameFragEvent event) {
    var killer = event.getKiller();
    if (killer == null || killer.getName().equals(event.getVictim().getName())) {
      return;
    }
    GameOrchestrator.getInstance()
        .getGameByPlayer(event.getKiller())
        .ifPresent(
            game -> {
              var score = game.getScore(event.getKiller());
              if (score == null) {
                return;
              }
              var streak = score.getStreak();
              var oldStreak = streak.copy();
              streak.setFragStreak(streak.getFragStreak() + 1);
              new GameStreakUpdateEvent(streak, oldStreak, event.getKiller(), game).fire();
            });
  }

  @EventHandler
  public void onFragApplyGibbing(GameFragEvent event) {
    if (event.isGibbed()) {
      gibbingSequence(event.getVictim(), event.getWeapon());
    }
  }

  @EventHandler
  public void onFrag(GameFragEvent event) {
    if (event.getWeapon() == Weapon.GAUNTLET) {
      var killer = event.getKiller();
      if (killer != null) {
        var sound =
            LangPlayer.of(killer).getLangContext().translateAnnounce(AnnouncerConstant.HUMILIATION);
        killer.playSound(killer, sound, .5f, 1f);
      }
    }
  }

  @EventHandler
  public void dropItemOnFrag(GameFragEvent event) {
    Item item;
    ItemStack itemStack = null;
    Player killer = event.getKiller();
    Player victim = event.getVictim();
    if (killer == null) return;
    Game game = GameOrchestrator.getInstance().getGameByPlayer(victim).orElse(null);
    if (game == null
        || game.getGamestage() != GameStage.IN_PROGRESS
        || !game.getRuleSet().getDefaultGameRules().dropMostValuableWeaponOnDeath()) return;
    Location deathLocation = victim.getLocation();

    for (int i = 7; i > 0 && itemStack == null; i--) {
      if (victim.getInventory().getItem(i) == null) continue;
      itemStack = Weapon.getBySlot(i).getPowerUp().getItemStack();
    }
    if (itemStack == null) return;

    itemStack.lore(List.of(Component.text(UUID.randomUUID().toString())));

    item =
        deathLocation
            .getWorld()
            .dropItemNaturally(
                deathLocation,
                itemStack,
                i -> {
                  i.getPersistentDataContainer()
                      .set(Keys.LOOT_POINT_KEY, PersistentDataType.INTEGER, -1);
                  i.setCanMobPickup(false);
                });

    Bukkit.getScheduler()
        .runTaskLater(
            ArenaShooter.getInstance(),
            item::remove,
            GameplayConstants.REMOVE_DEATH_DROP_AFTER_TICKS);
    item.setVelocity(new Vector(0f, .2f, 0f));
  }

  @EventHandler
  public void messageOnFrag(GameFragEvent event) {
    var killer = event.getKiller();
    var victim = event.getVictim();
    if (killer == null) return;

    Game game = GameOrchestrator.getInstance().getGameByPlayer(victim).orElse(null);
    if (game == null) return;
    if (killer.getName().equals(victim.getName())) {
      game.getPlayers().stream()
          .map(LangPlayer::new)
          .forEach(pl -> pl.sendRichLocalized("game.death.chat.self", victim.getName()));
      return;
    }

    var suffix =
        Optional.ofNullable(event.getWeapon()).map(w -> "." + w.translatableName).orElse("");

    game.getPlayers().stream()
        .map(LangPlayer::new)
        .forEach(
            pl ->
                pl.sendRichLocalized(
                    "game.death.chat.other" + suffix, killer.getName(), victim.getName()));
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

    Sound gibbingSound =
        Sound.sound()
            .source(Source.VOICE)
            .type(Key.key("entity.player.big_fall"))
            .volume(.5f)
            .build();
    eyeLoc.getWorld().playSound(gibbingSound, eyeLoc.getX(), eyeLoc.getY(), eyeLoc.getZ());

    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    head.editMeta(SkullMeta.class, meta -> meta.setOwningPlayer(victim));
    ItemStack bone = new ItemStack(Material.BONE);
    ItemStack meat = new ItemStack(Material.ROTTEN_FLESH);
    if (rng == null) {
      rng = new Random();
    }
    Stream.of(head, bone, meat, meat.clone(), meat.clone(), meat.clone())
        .map(itemStack -> world.dropItem(eyeLoc, itemStack))
        .forEach(
            item -> {
              item.setCanPlayerPickup(false);
              item.setVelocity(
                  item.getVelocity()
                      .add(
                          new Vector(
                              rng.nextDouble() - .5,
                              rng.nextDouble() - .5,
                              rng.nextDouble() - .5)));
              Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(), item::remove, 20 * 3L);
            });

    world.playSound(eyeLoc, org.bukkit.Sound.ENTITY_PLAYER_BIG_FALL, 1f, 1);
    world.spawnParticle(
        Particle.BLOCK_CRACK,
        eyeLoc,
        15,
        .5,
        .5,
        .5,
        .5,
        Material.REDSTONE_BLOCK.createBlockData());

    if (weapon == null) {
      return;
    }

    switch (weapon) {
      case ROCKET_LAUNCHER -> {
        world.spawnParticle(Particle.EXPLOSION_HUGE, eyeLoc, 1);
        world.playSound(eyeLoc, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1f, 2);
      }
      case RAILGUN -> {
        world.spawnParticle(Particle.ELECTRIC_SPARK, eyeLoc, 10, 0, 0, 0, .8);
        world.playSound(eyeLoc, org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, .4f, 2);
      }
      case GAUNTLET -> {
        world.spawnParticle(Particle.VILLAGER_ANGRY, eyeLoc, 10, .5, .5, .5, .8);
        world.playSound(eyeLoc, org.bukkit.Sound.ENTITY_PLAYER_ATTACK_SWEEP, .4f, 2);
      }
    }
  }
}
