package agency.shitcoding.arena.suggester;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public record SuggestionRule (
    @NotNull BiPredicate<CommandSender, String[]> condition,
    @NotNull Supplier<List<String>> suggestions
){}
