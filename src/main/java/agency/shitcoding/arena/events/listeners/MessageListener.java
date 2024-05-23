package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.ArenaShooter;
import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.gamestate.WeaponMods;
import agency.shitcoding.arena.localization.LangPlayer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;

public class MessageListener implements Listener {

  @EventHandler
  public void onPlayerMessageHandleCatSupremacyEasterEgg(AsyncChatEvent event) {

    Player player = event.getPlayer();

    String msg = plainText().serialize(event.message());

    LangPlayer langPlayer = LangPlayer.of(player);
    String cmp = langPlayer.getLocalized("easter.cat.prompt");

    if (msg.equalsIgnoreCase(cmp)) {
      Bukkit.getScheduler().runTask(ArenaShooter.getInstance(), () -> {
        CosmeticsService.getInstance().addWeaponMod(player, WeaponMods.getKittyCannon());
        player.sendRichMessage(langPlayer.getLocalized("easter.cat.message"));
      });
    }

  }
}
