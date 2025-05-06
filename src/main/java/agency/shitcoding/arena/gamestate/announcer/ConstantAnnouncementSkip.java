package agency.shitcoding.arena.gamestate.announcer;

import agency.shitcoding.arena.localization.SupportedLocale;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConstantAnnouncementSkip implements AnnouncementSkip {
  public static final long GRACE_TIME = 4L;
  private final Map<SupportedLocale, Long> skipMap;

  @Override
  public long getSkipTime(SupportedLocale locale) {
    var skipTime = skipMap.get(locale);
    if (skipTime == null) {
      // Fallback to default locale
      skipTime = skipMap.get(SupportedLocale.getDefault());
    }
    return skipTime;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Builder() {}

    private final Map<SupportedLocale, Long> skipMap = new HashMap<>();

    public Builder time(SupportedLocale locale, long skipTime) {
      skipMap.put(locale, skipTime);
      return this;
    }

    public Builder fromMillies(SupportedLocale locale, int milliseconds) {
      return time(locale, (long) (milliseconds / 1000f * 20));
    }

    public Builder addGraceTimeToAll() {
      skipMap.replaceAll((locale, skipTime) -> skipTime + GRACE_TIME);
      return this;
    }

    public ConstantAnnouncementSkip build() {
      return new ConstantAnnouncementSkip(skipMap);
    }
  }
}
