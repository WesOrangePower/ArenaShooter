package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Tournament;
import io.vavr.Lazy;
import java.util.Optional;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Setter
public class TournamentAccessor {
  private static final Lazy<TournamentAccessor> instance = Lazy.of(TournamentAccessor::new);
  private Tournament tournament = null;

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
    return instance.get();
  }

  private TournamentAccessor() {}
}
