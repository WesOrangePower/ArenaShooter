package agency.shitcoding.arena.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LootPointMarker {
  DEFAULT(1),
  RED_TEAM_SPAWN(1 << 1),
  BLUE_TEAM_SPAWN(1 << 2),
  RED_TEAM_BASE(1 << 3),
  BLUE_TEAM_BASE(1 << 4),
  TUTORIAL_MARKER(1 << 5);

  private final int value;
}
