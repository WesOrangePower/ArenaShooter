package agency.shitcoding.arena.storage.skips;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.Objects.requireNonNull;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.gamestate.announcer.AnnouncementSkip;
import agency.shitcoding.arena.gamestate.announcer.AnnouncementSkipProvider;
import agency.shitcoding.arena.gamestate.announcer.AnnouncerConstant;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.util.EnumMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.jspecify.annotations.Nullable;

public abstract class FileSkipProvider implements AnnouncementSkipProvider {
  protected final File file;
  private @Nullable Map<AnnouncerConstant, AnnouncementSkip> skipMap = null;
  private final WatchService watchService;

  protected FileSkipProvider(File file) {
    try {
      watchService = FileSystems.getDefault().newWatchService();
      file.getParentFile().toPath().register(watchService, ENTRY_MODIFY);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Bukkit.getScheduler()
        .runTaskTimer(
            ArenaShooter.getInstance(),
            () -> {
              var key = watchService.poll();
              if (key != null) {
                for (var event : key.pollEvents()) {
                  if (event.context().equals(file.getName())) {
                    load();
                  }
                }
              }
            },
            0,
            60);

    this.file = file;
    load();
  }

  protected abstract EnumMap<AnnouncerConstant, AnnouncementSkip> parse();

  private void load() {
    skipMap = parse();
  }

  @Override
  public AnnouncementSkip getAnnouncementSkip(AnnouncerConstant announcerConstant) {
    return requireNonNull(skipMap).get(announcerConstant);
  }
}
