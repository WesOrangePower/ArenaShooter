package agency.shitcoding.arena.suggester;

import org.bukkit.command.CommandSender;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface Suggester {
  @Nullable
  List<String> suggest(CommandSender sender, String[] args);

  Suggester combine(Suggester other);
}
