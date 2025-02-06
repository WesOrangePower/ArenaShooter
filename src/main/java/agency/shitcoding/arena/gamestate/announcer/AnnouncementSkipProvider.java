package agency.shitcoding.arena.gamestate.announcer;

@FunctionalInterface
public interface AnnouncementSkipProvider {
  AnnouncementSkip getAnnouncementSkip(AnnouncerConstant announcerConstant);
}
