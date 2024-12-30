package agency.shitcoding.arena.command.subcommands.arenamutation.processors;

import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.LootPoint;
import agency.shitcoding.arena.models.LootPointMarker;
import agency.shitcoding.arena.storage.StorageProvider;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import lombok.Data;
import org.bukkit.command.CommandSender;
import su.jellycraft.jellylib.models.Pair;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LootPointMarkerMutationProcessor extends AbstractProcessor {

  @Override
  public void remove(Arena arena, String value, CommandSender commandSender) {
    Validation<String, Pair<ArenaLootPointReference, LootPointMarker>> validatedValue =
        validateRemove(value, arena);
    if (validatedValue.isInvalid()) {
      commandSender.sendMessage(validatedValue.getError());
      return;
    }
    Pair<ArenaLootPointReference, LootPointMarker> pair = validatedValue.get();
    LootPoint lootPoint = pair.a.getLootPoint();
    lootPoint.setMarkers(lootPoint.getMarkers() & ~pair.b.getValue());

    commandSender.sendMessage(
        "Marker " + pair.b.name() + " removed from loot point " + pair.a.getLootPointId());
    StorageProvider.getArenaStorage().storeArena(arena);
  }

  @Override
  public void set(Arena arena, String value, CommandSender commandSender) {
    String[] args = value.split(",");
    if (args.length != 2) {
      commandSender.sendMessage("Invalid arguments. Expected: loot point id, marker name");
      return;
    }
    Validation<String, ArenaLootPointReference> validatedValue = validateGet(args[0], arena);
    if (validatedValue.isInvalid()) {
      commandSender.sendMessage(validatedValue.getError());
      return;
    }
    Validation<String, LootPointMarker> markerValidation = validateMarker(args[1]);
    if (markerValidation.isInvalid()) {
      commandSender.sendMessage(markerValidation.getError());
      return;
    }
    LootPoint lootPoint = validatedValue.get().getLootPoint();
    lootPoint.setMarkers(lootPoint.getMarkers() | markerValidation.get().getValue());

    commandSender.sendMessage(
        "Marker "
            + markerValidation.get().name()
            + " added to loot point "
            + validatedValue.get().getLootPointId());
    StorageProvider.getArenaStorage().storeArena(arena);
  }

  @Override
  public void get(Arena arena, String value, CommandSender commandSender) {
    var validatedValue = validateGet(value, arena);
    if (validatedValue.isInvalid()) {
      commandSender.sendMessage(validatedValue.getError());
      return;
    }

    commandSender.sendMessage(
        "Loot point "
            + value
            + " has markers: "
            + Arrays.stream(LootPointMarker.values())
                .filter(
                    marker ->
                        (validatedValue.get().getLootPoint().getMarkers() & marker.getValue()) > 0)
                .map(LootPointMarker::name)
                .collect(Collectors.joining(", ")));
  }

  private Validation<String, ArenaLootPointReference> validateGet(String lootPointId, Arena arena) {
    Validation<String, Integer> validatedInt = validateInt(lootPointId);
    if (validatedInt.isInvalid()) {
      return Validation.invalid(validatedInt.getError());
    }
    int intVal = validatedInt.get();
    var attempt = Try.of(() -> new ArenaLootPointReference(arena, intVal));
    if (attempt.isFailure()) {
      return Validation.invalid(attempt.getCause().getMessage());
    }
    return Validation.valid(attempt.get());
  }

  private Validation<String, Integer> validateInt(String value) {
    try {
      return Validation.valid(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      return Validation.invalid("Invalid loot point index");
    }
  }

  /**
   * @param value comma-separated [0] loot point id, [1] marker name
   */
  private Validation<String, Pair<ArenaLootPointReference, LootPointMarker>> validateRemove(
      String value, Arena arena) {
    String[] args = value.split(",");
    if (args.length != 2) {
      return Validation.invalid("Invalid arguments. Expected: loot point id, marker name");
    }
    Validation<String, ArenaLootPointReference> lootPointValidation = validateGet(args[0], arena);
    if (lootPointValidation.isInvalid()) {
      return Validation.invalid(lootPointValidation.getError());
    }
    Validation<String, LootPointMarker> markerValidation = validateMarker(args[1]);
    if (markerValidation.isInvalid()) {
      return Validation.invalid(markerValidation.getError());
    }
    return Validation.valid(new Pair<>(lootPointValidation.get(), markerValidation.get()));
  }

  private Validation<String, LootPointMarker> validateMarker(String markerName) {
    try {
      return Validation.valid(LootPointMarker.valueOf(markerName.toUpperCase()));
    } catch (IllegalArgumentException e) {
      return Validation.invalid(
          "Invalid marker name. Expected one of: "
              + Arrays.stream(LootPointMarker.values())
                  .map(LootPointMarker::name)
                  .collect(Collectors.joining(", ")));
    }
  }

  @Data
  static class ArenaLootPointReference {
    private final Arena arena;
    private final int lootPointId;
    private LootPoint lootPoint;

    public ArenaLootPointReference(Arena arena, int lootPointId) {
      this.arena = arena;
      this.lootPointId = lootPointId;
      this.lootPoint =
          arena.getLootPoints().stream()
              .filter(point -> point.getId() == lootPointId)
              .findFirst()
              .orElseThrow(
                  () -> new IllegalArgumentException("Loot point " + lootPointId + " not found"));
    }
  }
}
