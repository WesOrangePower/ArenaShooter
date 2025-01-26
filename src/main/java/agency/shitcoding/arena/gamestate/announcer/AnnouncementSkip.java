package agency.shitcoding.arena.gamestate.announcer;

import agency.shitcoding.arena.localization.SupportedLocale;

@FunctionalInterface
public interface AnnouncementSkip {
  long getSkipTime(SupportedLocale locale);
}
