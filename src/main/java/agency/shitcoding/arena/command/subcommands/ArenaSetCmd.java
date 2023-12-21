package agency.shitcoding.arena.command.subcommands;

import agency.shitcoding.arena.command.ArenaDeathMatchCommand;
import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetAction;
import agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetField;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.storage.StorageProvider;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class ArenaSetCmd extends CommandInst {
    public static final int ARG_ARENA = 1;
    public static final int ARG_ACTION = 2;
    public static final int ARG_FIELD = 3;
    public static final int ARG_VALUE = 4;
    public static final int MIN_ARGS = 4;
    public static final int ARGS_WITH_VALUE = 5;
    private Arena arena;
    private ArenaSetAction action;
    private ArenaSetField field;
    private String value;

    public ArenaSetCmd(@NotNull CommandSender sender, @NotNull String[] args) {
        super(sender, args);
    }

    @Override
    public void execute() {
        if (validate()) {
            setArena();
        }
    }

    private void setArena() {
        field.applyValue.accept(arena, action, value, sender);
    }

    private boolean validate() {
        String adminPerm = ArenaDeathMatchCommand.getAdminPerm();
        if (!sender.hasPermission(adminPerm)) {
            sender.sendRichMessage("<dark_red>У вас нет прав на использование этой подкоманды.");
            return false;
        }
        if (args.length < MIN_ARGS) {
            sender.sendRichMessage("<red>Not enough arguments. Usage: /arena set <arena> <action> <field> [value]");
            return false;
        }
        arena = StorageProvider.getArenaStorage().getArena(args[ARG_ARENA]);
        if (arena == null) {
            sender.sendRichMessage("<red>Arena " + args[ARG_ARENA] + " does not exist.");
            return false;
        }
        try {
            action = ArenaSetAction.valueOf(args[ARG_ACTION].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendRichMessage("<red>Invalid action. Valid actions are: " + Arrays.toString(ArenaSetAction.values()));
            return false;
        }

        try {
            field = ArenaSetField.valueOf(args[ARG_FIELD].toUpperCase());
        } catch (IllegalArgumentException e) {
            Collection<ArenaSetField> supportedFields = Arrays.stream(ArenaSetField.values())
                    .filter(f -> f.supports.test(action))
                    .toList();
            sender.sendRichMessage("<red>Invalid field. Valid fields are: " + supportedFields);
            return false;
        }

        if (!field.supports.test(action)) {
            sender.sendRichMessage("<red>Action not supported.");
        }

        if (args.length < ARGS_WITH_VALUE && action != ArenaSetAction.GET) {
            sender.sendRichMessage("<red>Not enough arguments. Usage: /arena set <arena> <action> <field> [value]");
            return false;
        }

        if (args.length > ARGS_WITH_VALUE) {
            sender.sendRichMessage("<red>Too many arguments. Usage: /arena set <arena> <action> <field> [value]");
            return false;
        }

        if (action != ArenaSetAction.GET) {
            value = args[ARG_VALUE];
        }

        return true;
    }

}
