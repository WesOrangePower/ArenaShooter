package agency.shitcoding.arena.gamestate;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import agency.shitcoding.arena.AnnouncerConstant;
import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.GameTeam;
import agency.shitcoding.arena.gamestate.team.TeamGame;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.*;
import agency.shitcoding.arena.worlds.ArenaWorld;
import java.util.Optional;
import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public class CTFGame extends TeamGame {
  @Getter private FlagManager flagManager;
  protected LootPointFilter lootPointFilter = this::lootPointFilter;

  @Override
  public LootPointFilter getLootPointFilter() {
    return lootPointFilter;
  }

  public CTFGame(ArenaWorld arenaWorld) {
    super(arenaWorld, RuleSet.CTF);

    var lps = arenaWorld.getShifted().getLootPoints();
    var hasRedTeamBase =
        lps.stream()
            .anyMatch(lp -> (lp.getMarkers() & LootPointMarker.RED_TEAM_BASE.getValue()) != 0);
    var hasBlueTeamBase =
        lps.stream()
            .anyMatch(lp -> (lp.getMarkers() & LootPointMarker.BLUE_TEAM_BASE.getValue()) != 0);
    var hasRedTeamSpawn =
        lps.stream()
            .anyMatch(lp -> (lp.getMarkers() & LootPointMarker.RED_TEAM_SPAWN.getValue()) != 0);
    var hasBlueTeamSpawn =
        lps.stream()
            .anyMatch(lp -> (lp.getMarkers() & LootPointMarker.BLUE_TEAM_SPAWN.getValue()) != 0);
    if (!hasRedTeamBase || !hasBlueTeamBase) {
      throw new IllegalStateException("CTF game must have both red and blue team bases");
    }
    if (!hasRedTeamSpawn || !hasBlueTeamSpawn) {
      throw new IllegalStateException("CTF game must have both red and blue team spawns");
    }
  }

  @Override
  protected void startGameStage2() {
    flagManager = new FlagManager(this);

    for (ETeam value : ETeam.values()) {
      Flag flag = flagManager.getFlag(value);
      flagManager.dropFlag(flag);
    }

    super.startGameStage2();
  }

  @Override
  public void updateScore(Player p, int delta) {
    /*
    Score does not update on kills
    */
  }

  public void onCtfScore(ETeam team, int delta) {
    var gameTeam = teamManager.getTeam(team);
    var score = teamManager.getScore(gameTeam);
    teamManager.setScore(gameTeam, Math.max(score + delta, 0));
    updateScoreBoard();
  }

  @Override
  public void removePlayer(Player player) {
    if (flagManager != null) {
      Flag carriedFlag = flagManager.getFlagByCarrier(player);
      if (carriedFlag != null) {
        flagManager.dropFlag(carriedFlag);
      }
    }
    super.removePlayer(player);
  }

  private boolean lootPointFilter(LootPoint lootPoint, Player player) {
    var team =
        getTeamManager()
            .getTeam(player)
            .orElseThrow(
                () -> new IllegalStateException("Player" + player.getName() + " not in team"));

    switch (team.getETeam()) {
      case BLUE -> {
        return (lootPoint.getMarkers() & LootPointMarker.BLUE_TEAM_SPAWN.getValue()) > 0;
      }
      case RED -> {
        return (lootPoint.getMarkers() & LootPointMarker.RED_TEAM_SPAWN.getValue()) > 0;
      }
    }
    return false;
  }

  @Override
  public void onPlayerDeath(Player player) {
    super.onPlayerDeath(player);
    if (gamestage != GameStage.IN_PROGRESS) {
      return;
    }

    Flag carriedFlag = flagManager.getFlagByCarrier(player);
    if (carriedFlag != null) {
      flagManager.dropAtPlayer(player, carriedFlag);
    }
  }

  /**
   *
   *
   * <pre>
   * Try to pick up the flag.
   * <ol>
   * <li>If flag is dropped, and the player is in the same team as the flag, reset flag</li>
   * <li>If flag is dropped, and the player is in the opposite team, pick up the flag</li>
   * <li>If flag is at base, and the player is in the same team as the flag, ignore</li>
   * <li>If flag is at base, and the player is in the same team as the flag, and carries enemy flag, score and reset flags</li>
   * <li>If flag is at base, and the player is in the opposite team, pick up the flag, play sound</li>
   * <li>If flag is carried, it should not be picked up, error out or remove item?</li>
   * </ol>
   * </pre>
   *
   * @param player the one who picked up the flag
   * @param item the flag item
   */
  public void tryPickupFlag(Player player, Item item) {
    Optional<GameTeam> teamOpt = getTeamManager().getTeam(player);
    if (teamOpt.isEmpty()) {
      return;
    }
    GameTeam playerTeam = teamOpt.get();
    GameTeam flagTeam =
        getTeamManager().getTeam(ETeam.getTeamByMaterial(item.getItemStack().getType()));
    Flag flag = flagManager.getFlag(flagTeam.getETeam());

    if (flag.isDropped()) {
      item.remove();
      if (playerTeam.getETeam() == flagTeam.getETeam()) {
        flagManager.reset(flag);
        announce(CTFMessageAction.FLAG_RETURNED, flagTeam.getETeam(), player);
      } else {
        flagManager.pickupFlag(player, flag);
        announce(CTFMessageAction.FLAG_TAKEN, flagTeam.getETeam(), player);
      }
      return;
    }

    if (flag.isAtBase()) {
      if (playerTeam.getETeam() != flagTeam.getETeam()) {
        flagManager.pickupFlag(player, flag);
        announce(CTFMessageAction.FLAG_TAKEN, flagTeam.getETeam(), player);
      }

      if (playerTeam.getETeam() == flagTeam.getETeam() && flagManager.carriesFlag(player)) {
        flagManager.score(player);
        flagManager.reset(requireNonNull(flagManager.getFlagByCarrier(player)));
        announce(CTFMessageAction.SCORES, flagTeam.getETeam(), player);
      }

      return;
    }

    if (flag.isCarried()) {
      item.remove();
      ArenaShooter.getInstance()
          .getComponentLogger()
          .error(
              Component.text(
                  "%s tried to pick up a carried flag".formatted(player.getName()),
                  NamedTextColor.YELLOW));
    }
  }

  protected void announceTeam(ETeam team, AnnouncerConstant key) {
    getTeamManager()
        .getTeam(team)
        .getPlayers()
        .forEach(
            p -> p.playSound(p, LangPlayer.of(p).getLangContext().translateAnnounce(key), 1f, 1f));
  }

  protected void announceTeam(
      ETeam team, String message, Function<LangPlayer, Object[]> additionalArgsExtractor) {
    getTeamManager().getTeam(team).getPlayers().stream()
        .map(LangPlayer::of)
        .forEach(p -> p.sendRichLocalized(message, additionalArgsExtractor.apply(p)));
  }

  protected void announceOtherTeams(ETeam team, AnnouncerConstant key) {
    getTeamManager().getTeams().values().stream()
        .filter(t -> t.getETeam() != team)
        .flatMap(t -> t.getPlayers().stream())
        .forEach(
            p -> p.playSound(p, LangPlayer.of(p).getLangContext().translateAnnounce(key), 1f, 1f));
  }

  protected void announceOtherTeams(
      ETeam team, String message, Function<LangPlayer, Object[]> additionalArgsExtractor) {
    getTeamManager().getTeams().values().stream()
        .filter(t -> t.getETeam() != team)
        .flatMap(t -> t.getPlayers().stream())
        .map(LangPlayer::of)
        .forEach(p -> p.sendRichLocalized(message, additionalArgsExtractor.apply(p)));
  }

  protected void announce(CTFMessageAction messageAction, ETeam selfTeam, Player actor) {
    announceTeam(selfTeam, messageAction.selfTeamAnnounceConstant);
    announceTeam(
        selfTeam,
        messageAction.selfTeamMessage,
        p -> messageAction.getAdditionalArguments(p, actor, this));
    announceOtherTeams(selfTeam, messageAction.otherTeamsAnnounceConstant);
    announceOtherTeams(
        selfTeam,
        messageAction.otherTeamsMessage,
        p -> messageAction.getAdditionalArguments(p, actor, this));
  }

  @RequiredArgsConstructor
  public enum CTFMessageAction {
    FLAG_TAKEN(
        "ctf.flag.taken",
        "ctf.flag.taken",
        AnnouncerConstant.YOUR_FLAG_TAKEN,
        AnnouncerConstant.ENEMY_FLAG_TAKEN),
    FLAG_RETURNED(
        "ctf.flag.returned",
        "ctf.flag.returned",
        AnnouncerConstant.YOUR_FLAG_RETURNED,
        AnnouncerConstant.ENEMY_FLAG_RETURNED),
    SCORES(
        "ctf.scores",
        "ctf.scores.your",
        AnnouncerConstant.YOUR_TEAM_SCORED,
        AnnouncerConstant.ENEMY_TEAM_SCORED);

    private final String selfTeamMessage;
    private final String otherTeamsMessage;
    private final AnnouncerConstant selfTeamAnnounceConstant;
    private final AnnouncerConstant otherTeamsAnnounceConstant;

    public Object[] getAdditionalArguments(LangPlayer audience, Player player, CTFGame game) {
      return new Object[] {
        player.getName(),
        game.getTeamManager()
            .getTeam(player)
            .map(GameTeam::getETeam)
            .map(ETeam::getTeamMeta)
            .map(
                meta ->
                    miniMessage()
                        .serialize(
                            Component.text(
                                audience.getLocalized(meta.getDisplayName()), meta.getTextColor())))
            .orElseThrow()
      };
    }
  }
}
