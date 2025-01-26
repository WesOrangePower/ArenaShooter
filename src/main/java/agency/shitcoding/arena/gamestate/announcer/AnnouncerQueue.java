package agency.shitcoding.arena.gamestate.announcer;

import java.util.Collection;
import org.bukkit.entity.Player;

public interface AnnouncerQueue {
  void announce(AnnouncerConstant constant, Player player);
  void announce(AnnouncerConstant constant, Collection<Player> players);
  void clear();
  void clear(Player player);
}
