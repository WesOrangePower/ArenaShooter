package agency.shitcoding.arena.gamestate;

import static agency.shitcoding.arena.GameplayConstants.GAME_END_TIMER_TICKS;

import agency.shitcoding.arena.AnnouncerConstant;
import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.events.MajorBuffTracker;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.*;
import agency.shitcoding.arena.statistics.GameOutcome;
import agency.shitcoding.arena.worlds.ArenaWorld;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public abstract class Game {
  protected final Scoreboard scoreboard;
  private final RespawnInvulnerability respawnInvulnerability = new RespawnInvulnerability();
  private final Set<Player> spectators = new HashSet<>();
  protected final Map<Player, Integer> statKills = new HashMap<>();
  protected final Map<Player, Integer> statDeaths = new HashMap<>();
  protected final Set<Player> players = new HashSet<>();
  protected final List<PlayerScore> scores = new ArrayList<>();
  protected final MajorBuffTracker majorBuffTracker = new MajorBuffTracker();
  protected final Set<Player> diedOnce = new HashSet<>();
  protected final Consumer<AnnouncerConstant> announcer =
      ac ->
          players.forEach(
              p ->
                  p.playSound(
                      p,
                      LangPlayer.of(p).getLangContext().translateAnnounce(ac),
                      SoundCategory.VOICE,
                      1f,
                      1f));
  protected Map<Player, BossBar> bossBarMap = new ConcurrentHashMap<>();
  protected RuleSet ruleSet;
  protected GameRules gameRules;
  protected Arena arena;
  protected ArenaWorld arenaWorld;
  protected BukkitTask gameTimerTask;
  protected BukkitTask ammoActionBarTask;
  protected GameStage gamestage = GameStage.WAITING;
  protected Objective scoreboardObjective;
  private Instant gameStart;
  private PlayerWaitingManager waitingManager;

  protected Game(ArenaWorld arenaWorld, RuleSet ruleSet, GameRules gameRules) {
    var arena = arenaWorld.getShifted();
    this.arenaWorld = arenaWorld;
    this.arena = arena;
    this.ruleSet = ruleSet;
    this.gameRules = gameRules;
    this.scoreboard = GameOrchestrator.getInstance().getScoreboard();
    arena
        .getLowerBound()
        .getWorld()
        .getNearbyEntities(BoundingBox.of(arena.getLowerBound(), arena.getUpperBound()))
        .stream()
        .filter(e -> e.getType() == EntityType.DROPPED_ITEM)
        .map(Item.class::cast)
        .forEach(
            item -> {
              item.getChunk().setForceLoaded(true);
              item.getChunk().load();
              item.remove();
            });
  }

  public void removePlayer(Player player) {
    players.remove(player);
    scores.removeIf(p -> p.getPlayer().equals(player));
    Optional.ofNullable(scoreboardObjective).ifPresent(o -> o.getScore(player).resetScore());
    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    Optional.ofNullable(bossBarMap.get(player))
        .ifPresent(
            bb -> {
              bb.removeViewer(player);
              bossBarMap.remove(player);
            });
    boolean isEmptyWaiting = gamestage == GameStage.WAITING && players.isEmpty();
    boolean isTooFewPlayers =
        gamestage == GameStage.IN_PROGRESS && players.size() < gameRules.minPlayers();
    if (isEmptyWaiting || isTooFewPlayers) {
      endGame("game.end.notEnoughPlayers", false);
    }
    players.forEach(p -> p.sendRichMessage(leaveBroadcastMessage(LangPlayer.of(p), player)));
    Lobby.getInstance().sendPlayer(player);
  }

  public void endGame(String reason, boolean intendedEnding, Object... toFormat) {
    this.gamestage = GameStage.FINISHED;
    if (waitingManager != null) {
      waitingManager.cleanup();
    }
    if (gameTimerTask != null) {
      gameTimerTask.cancel();
    }
    if (ammoActionBarTask != null) {
      ammoActionBarTask.cancel();
    }
    LootManagerProvider.cleanup(arena);

    Optional.ofNullable(scoreboardObjective).ifPresent(Objective::unregister);
    Bukkit.getScheduler()
        .runTaskLater(
            ArenaShooter.getInstance(),
            () -> {
              bossBarMap.forEach((player, bossBar) -> bossBar.removeViewer(player));
              bossBarMap.clear();
            },
            20L);

    if (intendedEnding) {
      Bukkit.getScheduler()
          .runTaskAsynchronously(
              ArenaShooter.getInstance(),
              () -> ArenaShooter.getInstance().getStatisticsService().endGame(getGameOutcomes()));
    }

    showEndGameTitle(reason, intendedEnding, toFormat);

    if (intendedEnding) {
      Bukkit.getScheduler()
          .runTaskLater(ArenaShooter.getInstance(), this::unregister, GAME_END_TIMER_TICKS);
    } else {
      unregister();
    }
  }

  protected void showEndGameTitle(String reason, boolean intendedEnding, Object[] toFormat) {
    Collections.sort(scores);

    for (Player player : players) {
      var langPlayer = new LangPlayer(player);
      langPlayer.sendRichLocalized("game.end.header");
      var stats = getGameStatComponent();
      player.sendMessage(stats);

      var localizedReason = langPlayer.getLocalized(reason, toFormat);
      langPlayer.sendRichLocalized("game.end.message", localizedReason);

      if (intendedEnding) {
        var place = scores.indexOf(getScore(player)) + 1;
        var title =
            Title.title(
                Component.text(localizedReason, NamedTextColor.GREEN),
                langPlayer.getRichLocalized("game.end.subtitle", place));
        player.showTitle(title);
      }
    }
  }

  private void unregister() {
    for (Player player : players) {
      Lobby.getInstance().sendPlayer(player);
    }
    TournamentAccessor.getInstance().getTournament().ifPresent(Tournament::endGame);
    GameOrchestrator.getInstance().removeGame(this);
  }

  protected abstract GameOutcome[] getGameOutcomes();

  protected abstract Component getGameStatComponent();

  public void forceStart() {
    if (gamestage != GameStage.WAITING) {
      return;
    }
    startGame();
  }

  public void startGame() {
    if (waitingManager != null) {
      waitingManager.cleanup();
    }
    announcer.accept(AnnouncerConstant.FIGHT);
    for (Player player : players) {
      LangPlayer.of(player).sendRichLocalized("game.start.message");
      arena.spawn(player, this, getLootPointFilter());
    }
    gamestage = GameStage.IN_PROGRESS;
    LootManagerProvider.create(this, arena, this::preprocessLootPoints);
    startGameStage2();
    this.gameStart = Instant.now();
    this.gameTimerTask =
        Bukkit.getScheduler()
            .runTaskTimer(ArenaShooter.getInstance(), this::onGameSecondElapsed, 0L, 20L);
    this.ammoActionBarTask =
        Bukkit.getScheduler()
            .runTaskTimerAsynchronously(
                ArenaShooter.getInstance(),
                () -> players.forEach(Ammo::displayAmmoActionBar),
                0L,
                30L);
    createScoreboardObjective();
  }

  /** Creates and sets main scoreboard objective */
  protected void createScoreboardObjective() {
    scoreboardObjective =
        scoreboard.registerNewObjective(
            arena.getName(),
            Criteria.DUMMY,
            Component.text("Score", NamedTextColor.GOLD),
            RenderType.INTEGER);
    scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

    for (Player p : players) {
      p.setScoreboard(scoreboard);
    }
    updateScoreBoard();
  }

  protected @NotNull Set<LootPoint> preprocessLootPoints(Set<LootPoint> lootPoints) {
    return lootPoints;
  }

  protected void onGameSecondElapsed() {
    long remainingSeconds =
        gameRules.gameLengthSeconds() - (Instant.now().getEpochSecond() - gameStart.getEpochSecond());
    float fractionBase = ((float) remainingSeconds) / gameRules.gameLengthSeconds();
    float fraction = Math.min(BossBar.MAX_PROGRESS, Math.max(BossBar.MIN_PROGRESS, fractionBase));
    boolean fractionIsOne = fraction < Vector.getEpsilon();

    timeEvents(remainingSeconds);

    if (fractionIsOne) {
      endGame("game.end.timeup", true);
    }

    long minutes = (remainingSeconds % 3600) / 60;
    long seconds = remainingSeconds % 60;

    String timeString = String.format("%02d:%02d", minutes, seconds);
    var mm = MiniMessage.miniMessage();
    for (Player player : players) {
      LangPlayer langPlayer = new LangPlayer(player);
      var name = mm.deserialize(langPlayer.getLocalized("game.bossbar.title", timeString));

      BossBar bossBar =
          bossBarMap.computeIfAbsent(
              player,
              p -> {
                var bb =
                    BossBar.bossBar(name, fraction, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
                bb.addViewer(Audience.audience(p));
                return bb;
              });
      bossBar.name(name);
      bossBar.progress(fraction);
    }
  }

  private void timeEvents(long remainingSeconds) {
    boolean fiveMinutes = remainingSeconds == 300;
    if (fiveMinutes) {
      announcer.accept(AnnouncerConstant.FIVE_MINUTE);
    }
    if (remainingSeconds == 60) {
      announcer.accept(AnnouncerConstant.ONE_MINUTE);
    }
    if (remainingSeconds == 3) {
      announcer.accept(AnnouncerConstant.THREE);
    }
    if (remainingSeconds == 2) {
      announcer.accept(AnnouncerConstant.TWO);
    }
    if (remainingSeconds == 1) {
      announcer.accept(AnnouncerConstant.ONE);
    }
  }

  protected void startGameStage2() {
    // Virtual method
  }

  public void addPlayer(Player player) {
    if (players.size() >= gameRules.maxPlayers()) {
      LangPlayer.of(player).sendRichLocalized("game.full");
    }
    if (waitingManager == null) {
      waitingManager = new PlayerWaitingManager(this);
      waitingManager.startAwaiting();
    }
    if (gamestage == GameStage.IN_PROGRESS) {
      diedOnce.add(player);
      updateScoreBoard();
      player.setScoreboard(scoreboard);
    }
    players.add(player);
    scores.add(new PlayerScore(0, player, new PlayerStreak()));
    player.sendRichMessage(youJoinedGameMessage(player));
    Set<Player> filteredPlayers =
        players.stream()
            .filter(p -> !p.getName().equals(player.getName()))
            .collect(Collectors.toUnmodifiableSet());
    sendJoinMessage(player, filteredPlayers);
    getArena().spawn(player, this, getLootPointFilter());
  }

  public String youJoinedGameMessage(Player p) {
    return LangPlayer.of(p).getLocalized("game.youJoined");
  }

  public void sendJoinMessage(Player player, Set<Player> players) {
    for (Player aPlayer : players) {
      LangPlayer.of(aPlayer).sendRichLocalized("game.playerJoined", player.getName());
    }
  }

  public String leaveBroadcastMessage(LangPlayer audience, Player player) {
    return audience.getLocalized("game.playerLeft", player.getName());
  }

  public @Nullable PlayerScore getScore(@Nullable Player p) {
    PlayerScore pScore = null;
    for (PlayerScore score : scores) {
      if (score.getPlayer().equals(p)) {
        pScore = score;
        break;
      }
    }
    return pScore;
  }

  public void recalculateScore() {
    var leadersBefore = leaders();
    Collections.sort(scores);
    var leadersAfter = leaders();
    updateScoreBoard();
    scoreSoundClue(leadersBefore, leadersAfter);
  }

  protected void updateScoreBoard() {
    if (scoreboardObjective == null) {
      return;
    }
    for (PlayerScore score : scores) {
      scoreboardObjective.getScore(score.getPlayer()).setScore(score.getScore());
    }
  }

  public void updateScore(Player p, int delta) {
    Collections.sort(scores);
    var leadersBefore = leaders();
    doUpdateScore(p, delta);
    var leadersAfter = leaders();

    scoreSoundClue(leadersBefore, leadersAfter);
  }

  protected void scoreSoundClue(Leaders leadersBefore, Leaders leadersAfter) {
    var lostLeaders = new HashSet<Player>();
    lostLeaders.addAll(leadersBefore.taken);
    lostLeaders.addAll(leadersBefore.tied);
    lostLeaders.removeAll(leadersAfter.taken);
    lostLeaders.removeAll(leadersAfter.tied);

    var takenLeaders = new HashSet<>(leadersAfter.taken);
    takenLeaders.removeAll(leadersBefore.taken);

    var tiedLeaders = new HashSet<>(leadersAfter.tied);
    tiedLeaders.removeAll(leadersBefore.tied);

    for (Player pl : takenLeaders) {
      playSound(pl, AnnouncerConstant.TAKEN_LEAD);
    }
    for (Player pl : tiedLeaders) {
      playSound(pl, AnnouncerConstant.TIED_LEAD);
    }
    for (Player pl : lostLeaders) {
      playSound(pl, AnnouncerConstant.LOSTLEAD);
    }
  }

  public void doUpdateScore(Player p, int delta) {
    if (scores.isEmpty()) {
      scores.add(new PlayerScore(delta, p, new PlayerStreak()));
      return;
    }
    PlayerScore pScore =
        Objects.requireNonNullElseGet(getScore(p), () -> new PlayerScore(0, p, new PlayerStreak()));
    // Don't go below 0
    if (pScore.getScore() + delta < 0) {
      return;
    }
    pScore.setScore(pScore.getScore() + delta);
    Collections.sort(scores);
    updateScoreBoard();
  }

  protected record Leaders(Set<Player> taken, Set<Player> tied) {}

  private Leaders leaders() {
    Set<Player> leaders = new HashSet<>();
    if (!scores.isEmpty()) {
      int topScore = scores.getFirst().getScore();
      if (topScore == 0) {
        return new Leaders(Collections.emptySet(), Collections.emptySet());
      }
      for (PlayerScore score : scores) {
        if (score.getScore() != topScore) {
          break;
        }
        leaders.add(score.getPlayer());
      }
    }
    if (leaders.size() == 1) {
      return new Leaders(leaders, Collections.emptySet());
    }
    return new Leaders(Collections.emptySet(), leaders);
  }

  public void onPlayerDeath(Player p) {
    if (gamestage != GameStage.IN_PROGRESS) {
      return;
    }
    incrementStat(statDeaths, p);
    diedOnce.add(p);
  }

  public void onKill(Player p) {
    incrementStat(statKills, p);
  }

  private void incrementStat(Map<Player, Integer> map, Player p) {
    Integer stat = map.get(p);
    if (stat == null) stat = 0;
    map.put(p, ++stat);
  }

  protected void playSound(Player p, String sound) {
    p.playSound(p, sound, SoundCategory.VOICE, .8f, 1f);
  }

  protected void playSound(Player p, AnnouncerConstant constant) {
    String sound = LangPlayer.of(p).getLangContext().translateAnnounce(constant);
    playSound(p, sound);
  }

  public LootPointFilter getLootPointFilter() {
    return (lootPoint, player) -> lootPoint.isSpawnPoint();
  }
}
