package agency.shitcoding.arena;

import com.github.yannicklamprecht.worldborder.api.IWorldBorder;
import com.github.yannicklamprecht.worldborder.api.WorldBorderAction;
import com.github.yannicklamprecht.worldborder.plugin.PersistenceWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @deprecated dependency hopelessly outdated
 */
@Deprecated(since = "1.1.1")
public class ArenaWorldBorderApi extends PersistenceWrapper {

  public ArenaWorldBorderApi(PersistenceWrapper worldBorderApi) {
    super(ArenaShooter.getInstance(), worldBorderApi);
  }

  public void sendRedScreen(Player player, long ticks) {
    IWorldBorder border = getWorldBorder(player);
    border.setWarningDistanceInBlocks((int) border.getSize());

    border.send(player, WorldBorderAction.SET_WARNING_BLOCKS);

    Bukkit.getScheduler().runTaskLater(ArenaShooter.getInstance(),
        () -> {
          border.setWarningDistanceInBlocks(0);
          border.send(player, WorldBorderAction.SET_WARNING_BLOCKS);
        }, ticks);
  }
}
