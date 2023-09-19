package agency.shitcoding.doublejump.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class CommandInst {

    protected final @NotNull CommandSender sender;
    protected final @NotNull String[] args;

    public CommandInst(@NotNull CommandSender sender, @NotNull String[] args) {
        this.sender = sender;
        this.args = args;
    }
    abstract void execute();
}
