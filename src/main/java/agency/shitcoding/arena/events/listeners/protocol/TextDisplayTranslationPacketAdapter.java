package agency.shitcoding.arena.events.listeners.protocol;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.localization.LangPlayer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.comphenix.protocol.wrappers.WrappedDataValue;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.entity.Player;

@SuppressWarnings("FieldCanBeLocal")
public class TextDisplayTranslationPacketAdapter extends PacketAdapter {
  private final Pattern I18N_PATTERN = Pattern.compile("#\\{(.+?)}");
  private final int TEXT_DISPLAY_METADATA_TEXT_FIELD = 23;

  public TextDisplayTranslationPacketAdapter() {
    super(
        ArenaShooter.getInstance(),
        ListenerPriority.NORMAL,
        PacketType.Play.Server.ENTITY_METADATA);
  }

  @Override
  public void onPacketSending(PacketEvent event) {
    Player player = event.getPlayer();
    if (player == null) {
      return;
    }

    var dataValueCollectionModifier = event.getPacket().getDataValueCollectionModifier();
    var dataValueList = dataValueCollectionModifier.readSafely(0);
    if (dataValueList == null || dataValueList.isEmpty()) {
      return;
    }

    for (int i = 0; i < dataValueList.size(); i++) {
      var dataValue = dataValueList.get(i);
      if (dataValue.getIndex() == TEXT_DISPLAY_METADATA_TEXT_FIELD
          && dataValue.getValue() instanceof WrappedChatComponent component) {
        Matcher matcher = I18N_PATTERN.matcher(component.getJson());
        if (matcher.find()) {
          var key = matcher.group(1);
          var localized = LangPlayer.of(player).getRichLocalized(key);

          var json = JSONComponentSerializer.json().serialize(localized);
          var newComponent = WrappedChatComponent.fromJson(json);
          var nativeComponent = newComponent.getHandle();
          var newDataValue = new WrappedDataValue(
              dataValue.getIndex(),
              dataValue.getSerializer(),
              nativeComponent
          );

          dataValueList.set(i, newDataValue);

          dataValueCollectionModifier.write(0, dataValueList);
          break;
        }
      }
    }
  }
}
