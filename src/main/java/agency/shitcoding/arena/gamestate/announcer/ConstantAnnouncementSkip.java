package agency.shitcoding.arena.gamestate.announcer;

import agency.shitcoding.arena.localization.SupportedLocale;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConstantAnnouncementSkip implements AnnouncementSkip {
  public static final long GRACE_TIME = 4L;
  private final Map<SupportedLocale, Long> skipMap;

  @Override
  public long getSkipTime(SupportedLocale locale) {
    var skipTime = skipMap.get(locale);
    if (skipTime == null) {
      throw new IllegalArgumentException("No mapping for locale: " + locale);
    }
    return skipTime;
  }

  static Builder builder() {
    return new Builder();
  }

  static class Builder {
    private Builder() {}

    private final Map<SupportedLocale, Long> skipMap = new HashMap<>();

    Builder time(SupportedLocale locale, long skipTime) {
      skipMap.put(locale, skipTime);
      return this;
    }

    Builder fromMillies(SupportedLocale locale, int milliseconds) {
      return time(locale, (long) (milliseconds / 1000f * 20));
    }

    Builder addGraceTimeToAll() {
      skipMap.replaceAll((locale, skipTime) -> skipTime + GRACE_TIME);
      return this;
    }

    ConstantAnnouncementSkip build() {
      return new ConstantAnnouncementSkip(skipMap);
    }
  }
}
