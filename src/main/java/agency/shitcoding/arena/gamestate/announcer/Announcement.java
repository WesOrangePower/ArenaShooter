package agency.shitcoding.arena.gamestate.announcer;

import agency.shitcoding.arena.localization.LangPlayer;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record Announcement(@NotNull AnnouncerConstant announcerConstant, @NotNull Player player) {
  public void announce() {
    announce(SoundCategory.VOICE, 1f, 1f);
  }

  public void announce(SoundCategory soundCategory, float volume, float pitch) {
    String sound = LangPlayer.of(player).getLangContext().translateAnnounce(announcerConstant);

    player.playSound(player, sound, soundCategory, volume, pitch);
  }
}
