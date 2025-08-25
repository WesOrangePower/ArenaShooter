package agency.shitcoding.arena.events.listeners.protocol;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.gui.TextInputClickAction;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AnvilTextInputPacketAdapter extends PacketAdapter {
  @Getter
  private static final Map<Player, String> currentInput = new HashMap<>();

  public AnvilTextInputPacketAdapter() {
    super(
        ArenaShooter.getInstance(),
        ListenerPriority.NORMAL,
        PacketType.Play.Client.ITEM_NAME,
        PacketType.Play.Client.CLOSE_WINDOW,
        PacketType.Play.Client.WINDOW_CLICK);
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
    var player = event.getPlayer();

    if (event.getPacketType() == PacketType.Play.Client.ITEM_NAME) {
      PacketContainer packet = event.getPacket();
      String flowingName = packet.getStrings().readSafely(0);
      currentInput.put(event.getPlayer(), flowingName);
      return;
    } else if (event.getPacketType() == PacketType.Play.Client.WINDOW_CLICK) {
      var input = currentInput.remove(event.getPlayer());
      var clickAction = TextInputClickAction.getRegistry().get(player);

      if (clickAction != null) {
        event.setCancelled(true);
        Bukkit.getScheduler()
            .runTask(ArenaShooter.getInstance(), () -> clickAction.resolve(player, input));
      }
    }

    currentInput.remove(player);
  }
}
