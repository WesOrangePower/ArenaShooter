package agency.shitcoding.arena.suggester;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Suggester {
  @Nullable
  List<String> suggest(@NotNull CommandSender sender, @NotNull String[] args);

  @NotNull
  Suggester combine(@NotNull Suggester other);
}
