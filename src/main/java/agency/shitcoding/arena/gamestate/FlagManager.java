package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.localization.LangContext;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.LootPointMarker;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class FlagManager {
  private final EnumMap<ETeam, Flag> flags = new EnumMap<>(ETeam.class);
  private final CTFGame game;

  public void dropFlag(Flag flag) {
    flag.setState(Flag.State.AT_BASE);
    flag.setCarrier(null);

    int flagMarker =
        switch (flag.getTeam()) {
          case RED -> LootPointMarker.RED_TEAM_BASE.getValue();
          case BLUE -> LootPointMarker.BLUE_TEAM_BASE.getValue();
        };

    game.getArena().getLootPoints().stream()
        .filter(lp -> (lp.getMarkers() & flagMarker) != 0)
        .forEach(
            lp ->
                lp.getLocation()
                    .getWorld()
                    .dropItem(
                        lp.getLocation(),
                        getFlagItem(flag),
                        item -> {
                          item.setGlowing(true);
                          item.setVelocity(Vector.fromJOML(new Vector3f(0f, 0.1f, 0f)));
                          item.getPersistentDataContainer()
                              .set(Keys.getFlagKey(), PersistentDataType.INTEGER, game.hashCode());
                          item.setCanMobPickup(false);
                        }));
  }

  public ItemStack getFlagItem(Flag flag) {
    var item = new ItemStack(flag.getTeam().getIcon());
    item.getItemMeta()
        .getPersistentDataContainer()
        .set(Keys.getFlagKey(), PersistentDataType.INTEGER, game.hashCode());
    item.lore(List.of(Component.text(UUID.randomUUID().toString())));
    return item;
  }

  public ItemStack getNamedFlagItem(LangContext lang, Flag flag) {
    var item = getFlagItem(flag);
    item.editMeta(
        meta ->
            meta.displayName(
                Component.text(
                    lang.getLocalized("ctf.flag." + flag.getTeam().name().toLowerCase()),
                    flag.getTeam().getTeamMeta().getTextColor())));
    return item;
  }

  public FlagManager(CTFGame game) {
    this.game = game;
    for (ETeam value : ETeam.values()) {
      flags.put(value, new Flag(value));
    }
  }

  public Flag getFlag(ETeam team) {
    return flags.get(team);
  }

  public boolean carriesFlag(Player player) {
    return getFlagByCarrier(player) != null;
  }

  public @Nullable Flag getFlagByCarrier(Player player) {
    return flags.values().stream()
        .filter(
            flag -> player.getName().equals(flag.getCarrier().map(Player::getName).orElse(null)))
        .findFirst()
        .orElse(null);
  }

  public void reset(Flag flag) {
    flag.getCarrier()
        .ifPresent(
            carrier -> {
              var helmet =
                  game.getTeamManager()
                      .getTeam(carrier)
                      .map(t -> t.getTeamMeta().getHelmet())
                      .orElse(null);
              carrier.getInventory().setHelmet(helmet);
              carrier.getInventory().setItem(8, null);
              carrier.removePotionEffect(PotionEffectType.GLOWING);
            });

    dropFlag(flag);
  }

  public void pickupFlag(Player player, Flag flag) {
    var team =
        game.getTeamManager()
            .getTeam(player)
            .orElseThrow(
                () -> new IllegalStateException("Player " + player.getName() + " not in team"));
    if (team.getETeam() == flag.getTeam()) {
      throw new IllegalStateException(
          "Player " + player.getName() + " cannot pick up flag of their own team");
    }
    if (player.getGameMode() != GameMode.ADVENTURE) {
      return;
    }

    flag.setState(Flag.State.CARRIED);
    flag.setCarrier(player);
    ItemStack namedFlagItem = getNamedFlagItem(LangPlayer.of(player).getLangContext(), flag);
    player.getInventory().setItem(8, namedFlagItem);
    player.getInventory().setHelmet(namedFlagItem);
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.GLOWING, 1000000, 1, false, false));
  }

  public void score(Player player) {
    var team =
        game.getTeamManager()
            .getTeam(player)
            .orElseThrow(
                () -> new IllegalStateException("Player " + player.getName() + " not in team"));

    game.onCtfScore(team.getETeam(), 1);
  }

  public Optional<Flag> getFlagByMaterial(@NotNull Material material) {
    return flags.values().stream().filter(flag -> flag.getTeam().getIcon() == material).findFirst();
  }

  public void dropAtPlayer(Player player, Flag carriedFlag) {
    carriedFlag.setState(Flag.State.DROPPED);
    carriedFlag.setCarrier(null);
    player.getInventory().setItem(8, null);
    player.getInventory().setHelmet(null);

    player
        .getLocation()
        .getWorld()
        .dropItem(
            player.getLocation(),
            getFlagItem(carriedFlag),
            item -> {
              item.setGlowing(true);
              item.setVelocity(Vector.fromJOML(new Vector3f(0f, 0.1f, 0f)));
              item.getPersistentDataContainer()
                  .set(Keys.getFlagKey(), PersistentDataType.INTEGER, game.hashCode());
              item.setCanMobPickup(false);
            });
  }
}
