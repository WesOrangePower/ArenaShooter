package agency.shitcoding.arena.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitTask;

@Data
@RequiredArgsConstructor
public class LootPointInstance {

  private final LootPoint lootPoint;
  private boolean looted;
  private BukkitTask spawnTask;
}
