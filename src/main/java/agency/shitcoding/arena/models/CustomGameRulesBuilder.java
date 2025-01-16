package agency.shitcoding.arena.models;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Is a builder for {@link CustomGameRules}. Field names should be the same as methods in {@link
 * GameRules} interface
 */
@SuppressWarnings("unused")
@Getter
public final class CustomGameRulesBuilder {
  private Map<Ammo, Integer> spawnAmmo = null;
  private List<Powerup> spawnPowerups = null;
  private Integer spawnArmor = null;
  private Long gameLengthSeconds = null;
  private Boolean doRespawn = null;
  private Boolean dropMostValuableWeaponOnDeath = null;
  private Integer maxPlayers = null;
  private Integer minPlayers = null;

  public static CustomGameRulesBuilder iWantToFillThemAllManually() {
    return new CustomGameRulesBuilder();
  }

  public static CustomGameRulesBuilder basedOn(GameRules baseGameRules) {
    var builder = new CustomGameRulesBuilder();

    var fields = CustomGameRulesBuilder.class.getDeclaredFields();

    for (var field : fields) {
      var setterName =
          "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

      try {
        var setter = CustomGameRulesBuilder.class.getDeclaredMethod(setterName, field.getType());
        var value = baseGameRules.getClass().getMethod(field.getName()).invoke(baseGameRules);
        setter.invoke(builder, value);
      } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    return builder;
  }

  public GameRules build() {
    return new CustomGameRules(
        spawnAmmo,
        spawnPowerups,
        spawnArmor,
        gameLengthSeconds,
        doRespawn,
        dropMostValuableWeaponOnDeath,
        maxPlayers,
        minPlayers);
  }

  private CustomGameRulesBuilder() {}

  public CustomGameRulesBuilder setSpawnAmmo(Map<Ammo, Integer> spawnAmmo) {
    this.spawnAmmo = spawnAmmo;
    return this;
  }

  public CustomGameRulesBuilder setSpawnPowerups(List<Powerup> spawnPowerups) {
    this.spawnPowerups = spawnPowerups;
    return this;
  }

  public CustomGameRulesBuilder setSpawnArmor(Integer spawnArmor) {
    this.spawnArmor = spawnArmor;
    return this;
  }

  public CustomGameRulesBuilder setGameLengthSeconds(Long gameLengthSeconds) {
    this.gameLengthSeconds = gameLengthSeconds;
    return this;
  }

  public CustomGameRulesBuilder setDoRespawn(Boolean doRespawn) {
    this.doRespawn = doRespawn;
    return this;
  }

  public CustomGameRulesBuilder setDropMostValuableWeaponOnDeath(
      Boolean dropMostValuableWeaponOnDeath) {
    this.dropMostValuableWeaponOnDeath = dropMostValuableWeaponOnDeath;
    return this;
  }

  public CustomGameRulesBuilder setMaxPlayers(Integer maxPlayers) {
    this.maxPlayers = maxPlayers;
    return this;
  }

  public CustomGameRulesBuilder setMinPlayers(Integer minPlayers) {
    this.minPlayers = minPlayers;
    return this;
  }
}
