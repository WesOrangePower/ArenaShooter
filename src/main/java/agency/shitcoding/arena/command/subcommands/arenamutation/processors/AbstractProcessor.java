package agency.shitcoding.arena.command.subcommands.arenamutation.processors;

import agency.shitcoding.arena.QuadConsumer;
import agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetAction;
import agency.shitcoding.arena.models.Arena;
import org.bukkit.command.CommandSender;

public abstract class AbstractProcessor
    implements QuadConsumer<Arena, ArenaSetAction, String, CommandSender> {
  @Override
  public void accept(
      Arena arena, ArenaSetAction arenaSetAction, String value, CommandSender commandSender) {
    switch (arenaSetAction) {
      case SET -> set(arena, value, commandSender);
      case ADD -> add(arena, value, commandSender);
      case REMOVE -> remove(arena, value, commandSender);
      case GET -> get(arena, value, commandSender);
    }
  }

  public void set(Arena arena, String value, CommandSender commandSender) {}

  public void add(Arena arena, String value, CommandSender commandSender) {}

  public void remove(Arena arena, String value, CommandSender commandSender) {}

  public void get(Arena arena, String value, CommandSender commandSender) {}
}
