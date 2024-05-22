package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.SoundConstants;
import agency.shitcoding.arena.events.GameDamageEvent;
import agency.shitcoding.arena.events.GameStreakUpdateEvent;
import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.gamestate.Game;
import agency.shitcoding.arena.gamestate.GameOrchestrator;
import agency.shitcoding.arena.gamestate.PlayerScore;
import agency.shitcoding.arena.localization.LangPlayer;
import agency.shitcoding.arena.models.Keys;
import agency.shitcoding.arena.models.PlayerStreak;
import agency.shitcoding.arena.models.Weapon;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class GameStreakListener implements Listener {

  @EventHandler
  public void announceRailStreak(GameStreakUpdateEvent event) {
    Player p = event.getPlayer();
    int streak = event.getStreak().getConsequentRailHit();
    int oldStreak = event.getOldStreak().getConsequentRailHit();
    if (oldStreak == streak) return;
    if (streak >= 2) {
      playSound(p, SoundConstants.IMPRESSIVE);
    }

    if (streak == 7) {
      if (CosmeticsService.getInstance()
          .getAvailableWeaponMods(p, Weapon.RAILGUN)
          .contains(Keys.getBubbleGunKey().getKey())) {
        return;
      }

      playSound(p, Sound.ENTITY_PLAYER_LEVELUP.key().value());
      LangPlayer.of(p).sendRichLocalized("easter.bubbleGun.message");
      CosmeticsService.getInstance().addWeaponMod(p, Keys.getBubbleGunKey());
    }

  }

  @EventHandler
  public void announceFragStreak(GameStreakUpdateEvent event) {
    Player p = event.getPlayer();
    int streak = event.getStreak().getFragStreak();
    int oldStreak = event.getOldStreak().getFragStreak();
    if (oldStreak == streak) return;
    switch (streak) {
      case 0, 1, 2 -> { /* Don't announce */ }
      case 3, 4 -> playSound(p, SoundConstants.EXCELLENT);
      default -> playSound(p, SoundConstants.HOLYSHIT);
    }
  }

  private void playSound(Player p, String sound) {
    p.playSound(p, sound, SoundCategory.VOICE, .8f, 1f);
  }
}
