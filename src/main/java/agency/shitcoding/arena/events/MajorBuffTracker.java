package agency.shitcoding.arena.events;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
public class MajorBuffTracker {
  private @Nullable Integer quadDamageTicks;
  private @Nullable Integer protectionTicks;
}
