package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Tournament;
import java.util.Optional;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
public class TournamentAccessor {
  private static @Nullable TournamentAccessor instance = null;
  private @Nullable Tournament tournament = null;

  public Optional<Tournament> getTournament() {
    return Optional.ofNullable(tournament);
  }

  public @Nullable Tournament getTournamentOrNull() {
    return tournament;
  }

  public boolean hasTournament() {
    return tournament != null;
  }

  public static TournamentAccessor getInstance() {
    return instance == null ? (instance = new TournamentAccessor()) : instance;
  }

  private TournamentAccessor() {}
}
