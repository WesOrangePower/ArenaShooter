package agency.shitcoding.arena.gamestate.tutorial;

import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.announcer.AnnouncementSkipProvider;
import agency.shitcoding.arena.gamestate.announcer.AnnouncerConstant;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Ammo;
import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.Powerup;
import io.papermc.paper.util.Tick;
import io.vavr.Lazy;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScriptBuilder {
  int elPointer = 0;
  @Getter private final List<ScriptAtom> scriptNodes = new ArrayList<>();
  @Setter private AnnouncementSkipProvider skipProvider = null;
  @Nullable private List<ScriptBuilder> children = null;

  public ScriptBuilder() {}

  public void run(Runnable r) {
    scriptNodes.add(new ScriptAtom.RunningScriptAtom(r));
    elPointer++;
  }

  public void teleport(Player player, Location loc) {
    run(() -> player.teleport(loc));
  }

  public void title(Player player, String titleKey, String subtitleKey) {
    run(
        () -> {
          var lp = LangPlayer.of(player);
          var titleComponent = lp.getRichLocalized(titleKey);
          var subtitleComponent = lp.getRichLocalized(subtitleKey);
          player.showTitle(Title.title(titleComponent, subtitleComponent));
        });
  }

  public void wait(Duration duration) {
    var now = Lazy.of(Instant::now);
    scriptNodes.add(
        new ScriptAtom.WaitingScriptAtom(
            () -> {
              var elapsed = Duration.between(now.get(), Instant.now());
              return elapsed.compareTo(duration) >= 0;
            }));
    elPointer++;
  }

  public void announce(Player player, AnnouncerConstant constant) {
    run(
        () -> {
          var lp = LangPlayer.of(player);
          var sound = lp.getLangContext().translateAnnounce(constant);
          player.playSound(player.getLocation(), sound, SoundCategory.VOICE, 1, 1);
        });
  }

  public void announceAndWait(Player player, AnnouncerConstant constant) {
    var lp = LangPlayer.of(player);
    announce(player, constant);
    var duration =
        Duration.of(
            skipProvider
                .getAnnouncementSkip(constant)
                .getSkipTime(lp.getLangContext().getSupportedLocale()),
            Tick.tick());
    wait(duration);
  }

  public void waitUntilInside(Player player, @NotNull BoundingBox box) {
    scriptNodes.add(
        new ScriptAtom.WaitingScriptAtom(
            () -> {
              var loc = player.getLocation().toVector();
              return box.contains(loc);
            }));
    elPointer++;
  }

  public void powerup(Player player, Powerup powerup) {
    run(() -> powerup.getOnPickup().apply(player));
  }

  public Collection<DummyReference<? extends LivingEntity>> spawnDummies(Location... locs) {
    var refs = new ArrayList<DummyReference<? extends LivingEntity>>();
    for (Location loc : locs) {
      refs.add(spawnDummy(loc));
    }
    return refs;
  }

  public DummyReference<? extends LivingEntity> spawnDummy(Location loc) {
    final Class<Zombie> dummyClass = Zombie.class;
    var dummyRef = new DummyReference<>(dummyClass, null);
    run(
        () ->
            loc.getWorld()
                .spawn(
                    loc,
                    dummyClass,
                    dummy -> {
                      dummyRef.setDummy(dummy);
                      dummy.setShouldBurnInDay(false);
                      dummy.setSilent(true);
                      dummy.setAdult();
                      dummy.clearLootTable();
                      dummy.getEquipment().clear();
                      dummy
                          .getPersistentDataContainer()
                          .set(Keys.noDropOnDeath(), PersistentDataType.BOOLEAN, true);
                    }));
    return dummyRef;
  }

  public void waitUntilGone(Collection<DummyReference<?>> refs) {
    refs.forEach(this::waitUntilGone);
  }

  public void waitUntilGone(DummyReference<?> ref) {
    scriptNodes.add(
        new ScriptAtom.WaitingScriptAtom(
            () -> {
              var dummy = ref.getDummy();
              if (dummy == null) {
                return true;
              }
              return !dummy.isValid();
            }));
    elPointer++;
  }

  public void maxAmmo(Player player) {
    run(() -> Ammo.maxAmmoForPlayer(player));
  }

  public void fork(Consumer<ScriptBuilder> consumer) {
    var sb = new ScriptBuilder();
    if (children == null) {
      children = new ArrayList<>();
    }
    children.add(sb);
    consumer.accept(sb);
    scriptNodes.add(new ScriptAtom.ForkingScriptAtom(sb));
    elPointer++;
  }

  public void openDoor(Player player, String doorId) {
    GameOrchestrator.getInstance()
        .getGameByPlayer(player)
        .map(
            game ->
                game.getArena().getDoors().stream()
                    .filter(door -> door.getId().equals(doorId))
                    .findFirst())
        .ifPresent(door -> run(() -> door.orElseThrow().open()));
  }

  public void mute(Player player) {
    run(() -> player.stopSound(SoundCategory.VOICE));
  }

  public void setHealth(Player player, double val) {
    run(() -> player.setHealth(val));
  }

  public void waitUntilHealthIsGreater(Player player, double val) {
    scriptNodes.add(new ScriptAtom.WaitingScriptAtom(() -> player.getHealth() >= val));
  }

  public void message(Player player, String localizationKey, Object... args) {
    run(() -> LangPlayer.of(player).sendRichLocalized(localizationKey, args));
  }

  public static class DummyReference<T extends LivingEntity> {
    @Getter @Setter private T dummy;

    public DummyReference(@SuppressWarnings("unused") Class<T> dummyClass, T o) {
      this.dummy = o;
    }
  }
}
