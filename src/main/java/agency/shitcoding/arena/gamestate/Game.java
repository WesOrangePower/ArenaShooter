package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.MajorBuffTracker;
import agency.shitcoding.arena.models.*;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public abstract class Game {
    protected final Scoreboard scoreboard;
    private final Set<Player> players = new HashSet<>();
    private final Set<Player> spectators = new HashSet<>();
    private final List<PlayerScore> scores = new ArrayList<>();
    private final MajorBuffTracker majorBuffTracker = new MajorBuffTracker();
    private final Set<Player> diedOnce = new HashSet<>();
    private final Consumer<String> announcer = s -> players.forEach(p -> p.playSound(p, s, SoundCategory.VOICE, .8f, 1f));
    protected RuleSet ruleSet;
    protected Arena arena;
    protected BukkitTask gameTimerTask;
    protected BukkitTask ammoActionBarTask;
    protected GameStage gamestage = GameStage.WAITING;
    private Instant gameStart;
    private BossBar bossBar;
    private Objective scoreboardObjective;
    private PlayerWaitingManager waitingManager = null;

    protected Game(Arena arena, RuleSet ruleSet) {
        this.arena = arena;
        this.ruleSet = ruleSet;
        this.scoreboard = GameOrchestrator.getInstance().getScoreboard();
        arena.getLowerBound().getWorld().getNearbyEntities(BoundingBox.of(arena.getLowerBound(), arena.getUpperBound()))
                .stream().filter(e -> e.getType() == EntityType.DROPPED_ITEM)
                .map(Item.class::cast)
                .forEach(item -> {
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
        if (bossBar != null) bossBar.removeViewer(Audience.audience(player));
        boolean isEmptyWaiting = gamestage == GameStage.WAITING && players.isEmpty();
        boolean isTooFewPlayers = gamestage == GameStage.IN_PROGRESS && players.size() < ruleSet.getMinPlayers();
        if (isEmptyWaiting || isTooFewPlayers) {
            endGame("Недостаточно игроков");
        }
        players.forEach(p -> p.sendRichMessage("<red>" + player.getName() + " вышел из игры"));
        Lobby.getInstance().sendPlayer(player);
    }

    public void endGame(String reason) {
        this.gamestage = GameStage.FINISHED;
        if (waitingManager != null) waitingManager.cleanup();
        if (gameTimerTask != null) gameTimerTask.cancel();
        if (ammoActionBarTask != null) ammoActionBarTask.cancel();
        if (bossBar != null) {
            StreamSupport.stream(bossBar.viewers().spliterator(), false)
                    .collect(Collectors.toUnmodifiableSet())
                    .forEach(viewer -> bossBar.removeViewer((Audience) viewer));
        }
        LootManagerProvider.cleanup(arena);

        Optional.ofNullable(scoreboardObjective).ifPresent(Objective::unregister);
        if (ruleSet == RuleSet.DM || ruleSet == RuleSet.INSTAGIB) {
            players.forEach(this::sendGameStats);
        }

        players.forEach(p -> p.sendRichMessage("<green><bold>Игра закончилась: " + reason));
        players.forEach(Lobby.getInstance()::sendPlayer);
        GameOrchestrator.getInstance().removeGame(this);
    }

    protected void sendGameStats(CommandSender sender) {
        sender.sendRichMessage("<green>-----Итоги----");
        Collections.sort(scores);
        for (PlayerScore score : scores) {
            String message = String.format("<green><bold>%s<gold>: <red>%d",
                    score.getPlayer().getName(),
                    score.getScore()
            );
            sender.sendRichMessage(message);
        }
    }

    public void startGame() {
        if (waitingManager != null) waitingManager.cleanup();
        for (Player player : players) {
            player.sendRichMessage("<green><bold>Игра началась");
            announcer.accept(SoundConstants.FIGHT);
            arena.spawn(player, this);
        }
        gamestage = GameStage.IN_PROGRESS;
        LootManagerProvider.create(this, arena, this::preprocessLootPoints);
        startGameStage2();
        this.gameStart = Instant.now();
        this.gameTimerTask = Bukkit.getScheduler().runTaskTimer(
                ArenaShooter.getInstance(),
                this::onGameSecondElapsed,
                0L, 20L
        );
        this.ammoActionBarTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                ArenaShooter.getInstance(),
                () -> players.forEach(Ammo::displayAmmoActionBar),
                0L, 30L
        );
        createScoreboardObjective();
    }

    /**
     * Creates and sets main scoreboard objective
     */
    protected void createScoreboardObjective() {
        scoreboardObjective = scoreboard.registerNewObjective(arena.getName(), Criteria.DUMMY,
                Component.text("Счёт", NamedTextColor.GOLD), RenderType.INTEGER);
        scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Player p : players) {
            p.setScoreboard(scoreboard);
            scoreboardObjective.getScore(p).setScore(0);
        }
    }

    protected @NotNull Set<LootPoint> preprocessLootPoints(Set<LootPoint> lootPoints) {
        return lootPoints;
    }

    protected void onGameSecondElapsed() {
        long remainingSeconds = ruleSet.getGameLenSeconds() - (Instant.now().getEpochSecond() - gameStart.getEpochSecond());
        float fraction = ((float) remainingSeconds) / ruleSet.getGameLenSeconds();
        fraction = Math.min(BossBar.MAX_PROGRESS, Math.max(BossBar.MIN_PROGRESS, fraction));
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        TextComponent title = Component.text("Осталось: " + timeString, TextColor.color(0xaa0000));
        boolean fractionIsOne = fraction < Vector.getEpsilon();

        timeEvents(remainingSeconds);

        if (fractionIsOne) {
            endGame("Время вышло");
        }

        if (bossBar == null) {
            this.bossBar = BossBar.bossBar(
                    title,
                    fraction,
                    BossBar.Color.RED,
                    BossBar.Overlay.PROGRESS
            );
            this.bossBar.addViewer(Audience.audience(players));
        }

        bossBar.progress(fraction);
        bossBar.name(title);
    }

    private void timeEvents(long remainingSeconds) {
        boolean fiveMinutes = remainingSeconds == 300;
        if (fiveMinutes) {
            announcer.accept(SoundConstants.FIVE_MINUTE);
        }
        if (remainingSeconds == 60) {
            announcer.accept(SoundConstants.ONE_MINUTE);
        }
        if (remainingSeconds == 3) {
            announcer.accept(SoundConstants.THREE);
        }
        if (remainingSeconds == 2) {
            announcer.accept(SoundConstants.TWO);
        }
        if (remainingSeconds == 1) {
            announcer.accept(SoundConstants.ONE);
        }
    }

    protected abstract void startGameStage2();

    public void addPlayer(Player player) {
        if (players.size() >= ruleSet.getMaxPlayers()) {
            player.sendRichMessage("<dark_red>Игра заполнена");
        }
        if (waitingManager == null) {
            waitingManager = new PlayerWaitingManager(this);
            waitingManager.startAwaiting();
        }
        if (gamestage == GameStage.IN_PROGRESS) {
            bossBar.addViewer(Audience.audience(player));
            diedOnce.add(player);
            scoreboardObjective.getScore(player).setScore(0);
            player.setScoreboard(scoreboard);
        }
        players.add(player);
        scores.add(new PlayerScore(0, player, new PlayerStreak()));
        player.sendRichMessage("<green>Вы присоединились к игре");
        players.forEach(p -> p.sendRichMessage("<green>" + player.getName() + " присоединился к игре"));
        getArena().spawn(player, this);
    }

    public Optional<PlayerScore> getOptScore(Player p) {
        return scores.stream()
                .filter(sc -> sc.getPlayer().equals(p))
                .findFirst();
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
        if (scoreboardObjective == null) return;
        for (PlayerScore score : scores) {
            scoreboardObjective.getScore(score.getPlayer()).setScore(score.getScore());
        }
    }

    public void updateScore(Player p, int delta) {
        Collections.sort(scores);
        var leadersBefore = leaders();
        updateScoreWithoutSound(p, delta);
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
            playSound(pl, SoundConstants.TAKEN_LEAD);
        }
        for (Player pl : tiedLeaders) {
            playSound(pl, SoundConstants.TIED_LEAD);
        }
        for (Player pl : lostLeaders) {
            playSound(pl, SoundConstants.LOSTLEAD);
        }
    }

    public void updateScoreWithoutSound(Player p, int delta) {
        if (scores.isEmpty()) {
            scores.add(new PlayerScore(delta, p, new PlayerStreak()));
            return;
        }
        PlayerScore pScore = Objects.requireNonNullElseGet(getScore(p),
                () -> new PlayerScore(0, p, new PlayerStreak()));
        // Don't go below 0
        if (pScore.getScore() + delta < 0) {
            return;
        }
        pScore.setScore(pScore.getScore() + delta);
        Collections.sort(scores);
        updateScoreBoard();
    }

    protected record Leaders(Set<Player> taken, Set<Player> tied) {
    }

    private Leaders leaders() {
        Set<Player> leaders = new HashSet<>();
        if (!scores.isEmpty()) {
            int topScore = scores.get(0).getScore();
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
        diedOnce.add(p);
    }

    protected void playSound(Player p, String sound) {
        p.playSound(p, sound, SoundCategory.VOICE, .8f, 1f);
    }
}
