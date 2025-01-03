package agency.shitcoding.arena.command.subcommands.arenamutation.processors;


import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.storage.StorageProvider;
import org.bukkit.command.CommandSender;

public class TagMutationProcessor extends AbstractProcessor {
  @Override
  public void add(Arena arena, String value, CommandSender commandSender) {
    arena.getTags().add(value);

    StorageProvider.getArenaStorage().storeArena(arena);
    commandSender.sendMessage("Tag " + value + " added to arena " + arena.getName());
  }

  @Override
  public void remove(Arena arena, String value, CommandSender commandSender) {
    arena.getTags().remove(value);

    StorageProvider.getArenaStorage().storeArena(arena);
    commandSender.sendMessage("Tag " + value + " removed from arena " + arena.getName());
  }

  @Override
  public void get(Arena arena, String value, CommandSender commandSender) {
    commandSender.sendMessage("Tags for arena " + arena.getName() + ": " + String.join(", ", arena.getTags()));
  }
}
