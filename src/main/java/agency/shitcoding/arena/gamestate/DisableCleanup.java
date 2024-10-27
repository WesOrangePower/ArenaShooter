package agency.shitcoding.arena.gamestate;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.storage.StorageProvider;
import agency.shitcoding.arena.worlds.WorldFactory;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.log4j.Log4j2;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

@Log4j2
public class DisableCleanup {

  public static void onShutdown() {
    WorldFactory.getInstance().cleanUp();
//    World lobbyWorld = Lobby.getInstance().getLocation().getWorld();
//
//    AtomicInteger trailingEntityCount = new AtomicInteger(0);
//    StorageProvider.getArenaStorage().getArenas().stream()
//        .map(Arena::getLowerBound)
//        .map(Location::getWorld)
//        .filter(w -> !lobbyWorld.getName().equals(w.getName()))
//        .flatMap(w -> {
//          var en = w.getEntities();
//          trailingEntityCount.incrementAndGet();
//          return en.stream();
//        })
//        .filter(e -> !e.getType().equals(EntityType.PLAYER))
//        .forEach(Entity::remove);
//
//    if (trailingEntityCount.get() > 0) {
//      log.info("Found {} entities in arena worlds and removed them",
//          trailingEntityCount.get());
//    }
  }

  private DisableCleanup() {}
}
