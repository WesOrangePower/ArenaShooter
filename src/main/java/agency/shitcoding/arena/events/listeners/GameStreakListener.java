package agency.shitcoding.arena.events.listeners;

import agency.shitcoding.arena.gamestate.announcer.AnnouncerConstant;
import agency.shitcoding.arena.events.GameStreakUpdateEvent;
import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.gamestate.WeaponMods;
import agency.shitcoding.arena.localization.LangPlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameStreakListener implements Listener {

  @EventHandler
  public void announceRailStreak(GameStreakUpdateEvent event) {
    Player p = event.getPlayer();
    int streak = event.getStreak().getConsequentRailHit();
    int oldStreak = event.getOldStreak().getConsequentRailHit();
    if (oldStreak == streak) return;
    if (streak >= 2) {
      playSound(p, AnnouncerConstant.IMPRESSIVE);
    }

    if (streak == 7) {
      if (CosmeticsService.getInstance().hasMod(p, WeaponMods.getBubbleGun())) {
        return;
      }

      playSound(p, Sound.ENTITY_PLAYER_LEVELUP.key().value());
      LangPlayer.of(p).sendRichLocalized("easter.bubbleGun.message");
      CosmeticsService.getInstance().addWeaponMod(p, WeaponMods.getBubbleGun());
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
      case 3, 4 -> playSound(p, AnnouncerConstant.EXCELLENT);
      default -> playSound(p, AnnouncerConstant.HOLYSHIT);
    }
  }

  private void playSound(Player p, String sound) {
    p.playSound(p, sound, SoundCategory.VOICE, .8f, 1f);
  }
  private void playSound(Player p, AnnouncerConstant constant) {
    var sound = LangPlayer.of(p).getLangContext().translateAnnounce(constant);
    playSound(p, sound);
  }
}
