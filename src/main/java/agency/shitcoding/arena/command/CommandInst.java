package agency.shitcoding.arena.command;

import agency.shitcoding.arena.localization.LangContext;
import agency.shitcoding.arena.localization.LangPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommandInst {

  protected final @NotNull CommandSender sender;
  protected final @NotNull String[] args;

  protected CommandInst(@NotNull CommandSender sender, @NotNull String[] args) {
    this.sender = sender;
    this.args = args;
  }

  public abstract void execute();

  protected void sendRich(@NotNull String key, @NotNull Object... args) {
    sender.sendMessage(translateRich(key, args));
  }

  protected @NotNull String translatePlain(@NotNull String key, @NotNull Object... args) {
    return getLangContext().getLocalized(key, args);
  }

  protected @NotNull Component translateRich(@NotNull String key, @NotNull Object... args) {
    return getLangContext().getRichLocalized(key, args);
  }

  protected @NotNull LangContext getLangContext () {
    return sender instanceof Player p ? LangPlayer.of(p).getLangContext() : LangContext.DEFAULT;
  }

  protected @Nullable String argOrNull(int i) {
    return i < args.length ? args[i] : null;
  }
}
