package agency.shitcoding.arena.suggester;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.Nullable;

public record SuggestionRule (
    BiPredicate<CommandSender, String[]> condition,
    Supplier<@Nullable List<String>> suggestions
){}
