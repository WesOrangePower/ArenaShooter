package agency.shitcoding.arena.command;

import agency.shitcoding.arena.localization.LangContext;
import agency.shitcoding.arena.localization.LangPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public abstract class CommandInst {

  protected final CommandSender sender;
  protected final String[] args;

  protected CommandInst(CommandSender sender, String[] args) {
    this.sender = sender;
    this.args = args;
  }

  public abstract void execute();

  protected void sendRich(String key, Object... args) {
    sender.sendMessage(translateRich(key, args));
  }

  protected String translatePlain(String key, Object... args) {
    return getLangContext().getLocalized(key, args);
  }

  protected Component translateRich(String key, Object... args) {
    return getLangContext().getRichLocalized(key, args);
  }

  protected LangContext getLangContext () {
    return sender instanceof Player p ? LangPlayer.of(p).getLangContext() : LangContext.DEFAULT;
  }

  protected @Nullable String argOrNull(int i) {
    return i < args.length ? args[i] : null;
  }
}
