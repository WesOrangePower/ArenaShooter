package agency.shitcoding.arena.command.subcommands.arenamutation;

import static agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetAction.ADD;
import static agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetAction.GET;
import static agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetAction.REMOVE;
import static agency.shitcoding.arena.command.subcommands.arenamutation.ArenaSetAction.SET;

import agency.shitcoding.arena.QuadConsumer;
import agency.shitcoding.arena.models.Arena;
import agency.shitcoding.arena.models.LootPoint;
import agency.shitcoding.arena.models.Portal;
import agency.shitcoding.arena.models.Powerup;
import agency.shitcoding.arena.models.Ramp;
import agency.shitcoding.arena.models.door.Door;
import agency.shitcoding.arena.models.door.DoorTrigger;
import agency.shitcoding.arena.storage.ArenaStorage;
import agency.shitcoding.arena.storage.StorageProvider;
import agency.shitcoding.arena.worlds.WorldFactory;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public enum ArenaSetField {
  NAME(
      a -> a == SET,
      (ar, a, v, p) -> {
        if (v == null || !v.matches("[a-zA-Z0-9_]{3,16}")) {
          p.sendRichMessage(
              "<red>Invalid arena name. Must be between 3 and 16 characters long and contain only letters, numbers and underscores.");
          return;
        }
        ArenaStorage arenaStorage = StorageProvider.getArenaStorage();
        if (arenaStorage.getArena(v) != null) {
          p.sendRichMessage("<red>Arena with that name already exists.");
          return;
        }
        arenaStorage.deleteArena(ar);
        ar.setName(v);
        arenaStorage.storeArena(ar);
        p.sendRichMessage("<green>Arena name changed to " + v);
      }),
  POWERUP(
      a -> a == ADD || a == REMOVE || a == GET,
      (ar, a, v, p) -> {
        ArenaStorage arenaStorage = StorageProvider.getArenaStorage();
        switch (a) {
          case ADD -> {
            Powerup powerup;
            try {
              powerup = Powerup.valueOf(v);
            } catch (IllegalArgumentException e) {
              p.sendRichMessage(
                  "<red>Invalid powerup name. Valid powerups are: "
                      + Arrays.toString(Powerup.values()));
              return;
            }
            Location centerLocation = unshifted(((Player) p).getLocation().toCenterLocation());
            LootPoint lootPoint = new LootPoint(ar.getLootPoints().size(), centerLocation, powerup);
            ar.getLootPoints().add(lootPoint);
            arenaStorage.storeArena(ar);
            p.sendRichMessage(
                "<green>Powerup "
                    + powerup.name()
                    + " added to arena "
                    + ar.getName()
                    + " at "
                    + centerLocation);
          }
          case GET ->
              ar.getLootPoints()
                  .forEach(
                      lp ->
                          p.sendMessage(
                              Component.text(
                                      "Point " + lp.getId() + ": " + lp.getType().name() + " at ")
                                  .append(locationComponent(lp.getLocation()))));
          case REMOVE -> {
            if (v == null) {
              p.sendRichMessage("<red>Powerup ID must be specified.");
              return;
            }
            int id;
            try {
              id = Integer.parseInt(v);
            } catch (NumberFormatException e) {
              p.sendRichMessage("<red>Invalid powerup ID. Must be a number.");
              return;
            }
            ar.getLootPoints().stream()
                .filter(lp -> lp.getId() == id)
                .findFirst()
                .ifPresentOrElse(
                    lp -> {
                      ar.getLootPoints().remove(lp);
                      arenaStorage.storeArena(ar);
                      p.sendRichMessage("<green>Powerup " + lp.getType().name() + " removed.");
                    },
                    () -> p.sendRichMessage("<red>Powerup " + id + " not found."));
          }
        }
      }),
  LBOUND(
      a -> a == GET || a == SET,
      (ar, a, v, s) -> {
        if (a == GET) {
          sendBoundaries(ar, s);
          return;
        }
        if (a == SET) {
          if (!(s instanceof Player player)) {
            s.sendMessage("Players only");
            return;
          }
          Location upper = ar.getUpperBound();
          Location location = unshifted(player.getLocation().toCenterLocation());

          if (location.getBlockX() >= upper.getBlockX()
              || location.getBlockY() >= upper.getBlockY()
              || location.getBlockZ() >= upper.getBlockZ()) {
            s.sendRichMessage(
                "<dark_red>Lower boundary cannot have higher coordinates than upper boundary. Perhaps you meant UBOUND?");
            s.sendMessage(
                locationComponent(location)
                    .append(Component.text(" ≥ ", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                    .append(locationComponent(upper)));
            return;
          }
          ar.setLowerBound(location);
          StorageProvider.getArenaStorage().storeArena(ar);
          s.sendMessage(
              Component.text("OK. Set lower bound to ", NamedTextColor.GREEN)
                  .append(locationComponent(location)));
        }
      }),
  UBOUND(
      a -> a == GET || a == SET,
      (ar, a, v, s) -> {
        if (a == GET) {
          sendBoundaries(ar, s);
          return;
        }
        if (a == SET) {
          if (!(s instanceof Player player)) {
            s.sendMessage("Players only");
            return;
          }
          Location lower = ar.getLowerBound();
          Location location = unshifted(player.getLocation().toCenterLocation());

          if (location.getBlockX() <= lower.getBlockX()
              || location.getBlockY() <= lower.getBlockY()
              || location.getBlockZ() <= lower.getBlockZ()) {
            s.sendRichMessage(
                "<dark_red>Upper boundary cannot have lower coordinates than lower boundary. Perhaps you meant LBOUND?");
            s.sendMessage(
                locationComponent(location)
                    .append(Component.text(" ≤ ", NamedTextColor.DARK_RED, TextDecoration.BOLD))
                    .append(locationComponent(lower)));
            return;
          }

          ar.setUpperBound(location);
          StorageProvider.getArenaStorage().storeArena(ar);
          s.sendMessage(
              Component.text("OK. Set upper bound to ", NamedTextColor.GREEN)
                  .append(locationComponent(location)));
        }
      }),
  PORTAL(
      a -> a == SET || a == GET || a == REMOVE,
      (ar, a, v, s) -> {
        if (a == SET) {
          Component usage =
              Component.text(
                  "Usage: /arena set <arena> portal <action> <id>,<x1>,<y1>,<z1>,<x2>,<y2>,<z2>,<xt>,<yt>,<zt>",
                  NamedTextColor.DARK_RED);
          if (v == null) {
            s.sendMessage(usage);
            return;
          }
          String[] split = v.split(",");
          if (split.length < 10) {
            s.sendMessage(usage);
            return;
          }
          String id = split[0];
          Location firstLocation =
              new Location(
                  ar.getLowerBound().getWorld(),
                  Integer.parseInt(split[1]),
                  Integer.parseInt(split[2]),
                  Integer.parseInt(split[3]));
          Location secondLocation =
              new Location(
                  ar.getLowerBound().getWorld(),
                  Integer.parseInt(split[4]),
                  Integer.parseInt(split[5]),
                  Integer.parseInt(split[6]));
          Location targetLocation =
              new Location(
                  ar.getLowerBound().getWorld(),
                  Integer.parseInt(split[7]),
                  Integer.parseInt(split[8]),
                  Integer.parseInt(split[9]));
          Portal portal = new Portal(id, firstLocation, secondLocation, targetLocation);
          ar.getPortals().add(portal);
          StorageProvider.getArenaStorage().storeArena(ar);
          s.sendRichMessage("<green>Portal " + id + " added.");
          return;
        }
        if (a == GET) {
          for (Portal portal : ar.getPortals()) {
            Component component =
                Component.text("Portal " + portal.getId() + " at ")
                    .append(locationComponent(portal.getFirstLocation()))
                    .append(Component.text(" and "))
                    .append(locationComponent(portal.getSecondLocation()))
                    .append(Component.text(" leads to "))
                    .append(locationComponent(portal.getTargetLocation()));
            s.sendMessage(component);
          }
          return;
        }
        if (a == REMOVE) {
          if (v == null) {
            s.sendRichMessage("<red>Portal ID must be specified.");
            return;
          }
          ar.getPortals().stream()
              .filter(p -> p.getId().equals(v))
              .findFirst()
              .ifPresentOrElse(
                  portal -> {
                    ar.getPortals().remove(portal);
                    StorageProvider.getArenaStorage().storeArena(ar);
                    s.sendRichMessage("<green>Portal " + v + " removed.");
                  },
                  () -> s.sendRichMessage("<red>Portal " + v + " not found."));
        }
      }),
  RAMP(
      a -> a == SET || a == GET || a == REMOVE,
      (ar, a, v, s) -> {
        if (a == SET) {
          Component usage =
              Component.text(
                  "Usage: /arena set <arena> ramp <action> <id>,<x1>,<y1>,<z1>,<x2>,<y2>,<z2>,<MUL/NOMUL>,<vx>,<vy>,<vz>",
                  NamedTextColor.DARK_RED);
          if (v == null) {
            s.sendMessage(usage);
            return;
          }
          String[] split = v.split(",");
          if (split.length < 11) {
            s.sendMessage(usage);
            return;
          }
          String id = "RP_" + split[0];
          Location firstLocation =
              new Location(
                  ar.getLowerBound().getWorld(),
                  Integer.parseInt(split[1]),
                  Integer.parseInt(split[2]),
                  Integer.parseInt(split[3]));
          Location secondLocation =
              new Location(
                  ar.getLowerBound().getWorld(),
                  Integer.parseInt(split[4]),
                  Integer.parseInt(split[5]),
                  Integer.parseInt(split[6]));
          Boolean multiply =
              switch (split[7].toUpperCase()) {
                case "MUL" -> true;
                case "NOMUL" -> false;
                default -> {
                  s.sendRichMessage(
                      "<red>Invalid value for MUL/NOMUL. Must be either MUL or NOMUL.");
                  yield null;
                }
              };
          Vector vector =
              new Vector(
                  Double.parseDouble(split[8]),
                  Double.parseDouble(split[9]),
                  Double.parseDouble(split[10]));
          Ramp ramp =
              new Ramp(id, firstLocation, secondLocation, Boolean.TRUE.equals(multiply), vector);
          ar.getRamps().removeIf(r -> r.getId().equals(id));
          ar.getRamps().add(ramp);
          StorageProvider.getArenaStorage().storeArena(ar);
          s.sendRichMessage("<green>ramp " + id + " added.");
          return;
        }
        if (a == GET) {
          for (Ramp ramp : ar.getRamps()) {
            Component component =
                Component.text("Ramp " + ramp.getId() + " at ")
                    .append(locationComponent(ramp.getFirstLocation()))
                    .append(Component.text(" and "))
                    .append(locationComponent(ramp.getSecondLocation()))
                    .append(
                        Component.text(
                            ramp.isMultiply() ? " multiplies by " : " sets velocity to "))
                    .append(Component.text(ramp.getVector().toString()));
            s.sendMessage(component);
          }
          return;
        }
        if (a == REMOVE) {
          if (v == null) {
            s.sendRichMessage("<red>Ramp ID must be specified.");
            return;
          }
          ar.getRamps().stream()
              .filter(r -> r.getId().equals(v))
              .findFirst()
              .ifPresentOrElse(
                  ramp -> {
                    ar.getRamps().remove(ramp);
                    StorageProvider.getArenaStorage().storeArena(ar);
                    s.sendRichMessage("<green>Ramp " + v + " removed.");
                  },
                  () -> s.sendRichMessage("<red>Ramp " + v + " not found."));
        }
      }),
  ALLOW_HOST(
      a -> a == SET || a == GET,
      (ar, a, v, s) -> {
        if (a == GET) {
          s.sendMessage("Allow host: " + ar.isAllowHost());
          return;
        }
        if (a == SET) {
          if (v == null || !v.matches("true|false")) {
            s.sendRichMessage("<red>Invalid value. Must be `true` or `false`.");
            return;
          }
          boolean allowHost = Boolean.parseBoolean(v);
          ar.setAllowHost(allowHost);
          StorageProvider.getArenaStorage().storeArena(ar);
          s.sendRichMessage("<green>Allow host set to " + allowHost);
        }
      }),
  AUTHORS(
      a -> a == SET || a == GET,
      (ar, a, v, s) -> {
        if (a == GET) {
          s.sendMessage("Authors: " + ar.getAuthors());
          return;
        }
        if (a == SET) {
          if (v == null) {
            s.sendRichMessage(
                "<red>Authors must be specified. Use /arena set <arena> authors <author1,author2,...>.");
            return;
          }
          List<String> newAuthors = Arrays.asList(v.split(","));
          ar.setAuthors(newAuthors);
          StorageProvider.getArenaStorage().storeArena(ar);
          s.sendRichMessage("<green>Authors set to " + newAuthors);
        }
      }),
  DOOR(
      a -> a == ADD || a == REMOVE || a == GET,
      (ar, a, v, s) -> {
        switch (a) {
          case ADD -> {
            final String usage =
                "<red>Use: /arena set <arena> door add <id>,<type>,<x1>,<y1>,<z1>,<x2>,<y2>,<z2>,<xdc>,<ydc>,<zdc>,<animationTime>,<closeAfter>";
            if (v == null) {
              s.sendRichMessage(usage);
              return;
            }
            String[] split = v.split(",");
            if (split.length < 13) {
              s.sendRichMessage(usage);
              return;
            }
            String id = split[0];
            int type = Integer.parseInt(split[1]);
            Location edge1 =
                new Location(
                    ar.getLowerBound().getWorld(),
                    Integer.parseInt(split[2]),
                    Integer.parseInt(split[3]),
                    Integer.parseInt(split[4]));
            Location edge2 =
                new Location(
                    ar.getLowerBound().getWorld(),
                    Integer.parseInt(split[5]),
                    Integer.parseInt(split[6]),
                    Integer.parseInt(split[7]));
            Location destinationCenter =
                new Location(
                    ar.getLowerBound().getWorld(),
                    Double.parseDouble(split[8]),
                    Double.parseDouble(split[9]),
                    Double.parseDouble(split[10]));
            int animationTime = Integer.parseInt(split[11]);
            int closeAfter = Integer.parseInt(split[12]);
            Door door =
                new Door(
                    id, type, animationTime, closeAfter, false, edge1, edge2, destinationCenter);
            ar.getDoors().add(door);
            StorageProvider.getArenaStorage().storeArena(ar);
            s.sendRichMessage("<green>Door " + id + " added.");
          }
          case GET -> {
            for (Door door : ar.getDoors()) {
              Component component =
                  Component.text(
                          "Door " + door.getDoorId() + " type " + door.getDoorType() + " at ")
                      .append(locationComponent(door.getEdge1()))
                      .append(Component.text(" and "))
                      .append(locationComponent(door.getEdge2()))
                      .append(Component.text(" leads to "))
                      .append(locationComponent(door.getDestinationCenter()))
                      .appendNewline()
                      .append(Component.text(" with animation time "))
                      .append(Component.text(door.getAnimationTime()))
                      .appendNewline()
                      .append(Component.text(" and close after "))
                      .append(Component.text(door.getCloseAfterTicks()))
                      .append(Component.text(" ticks."));
              s.sendMessage(component);
            }
          }
          case REMOVE -> {
            if (v == null) {
              s.sendRichMessage("<red>Door ID must be specified.");
              return;
            }

            ar.getDoors().stream()
                .filter(d -> d.getDoorId().equals(v))
                .findFirst()
                .ifPresentOrElse(
                    door -> {
                      ar.getDoors().remove(door);
                      StorageProvider.getArenaStorage().storeArena(ar);
                      s.sendRichMessage("<green>Door " + v + " removed.");
                    },
                    () -> s.sendRichMessage("<red>Door " + v + " not found."));
          }
        }
      }),
  TRIGGER(
      a -> a == ADD || a == REMOVE || a == GET,
      (ar, a, v, s) -> {
        switch (a) {
          case ADD -> {
            final String usage =
                "<red>Use: /arena set <arena> add TRIGGER <id>,<type>,<x>,<y>,<z>,<door_ids...>";
            if (v == null) {
              s.sendRichMessage(usage);
              return;
            }
            String[] split = v.split(",");
            if (split.length < 5) {
              s.sendRichMessage(usage);
              return;
            }
            String id = split[0];
            int type = Integer.parseInt(split[1]);
            Location location =
                new Location(
                    ar.getLowerBound().getWorld(),
                    Integer.parseInt(split[2]),
                    Integer.parseInt(split[3]),
                    Integer.parseInt(split[4]));
            List<String> doorIds = Arrays.asList(split).subList(5, split.length);
            ar.getDoorTriggers().add(new DoorTrigger(id, doorIds, type, location));
            StorageProvider.getArenaStorage().storeArena(ar);
            s.sendRichMessage("<green>Trigger " + id + " added.");
          }
          case GET -> {
            for (DoorTrigger trigger : ar.getDoorTriggers()) {
              Component component =
                  Component.text(
                          "Trigger "
                              + trigger.getTriggerId()
                              + " type "
                              + trigger.getTriggerType()
                              + " at ")
                      .append(locationComponent(trigger.getLocation()))
                      .append(Component.text(" triggers doors "))
                      .append(Component.text(String.join(", ", trigger.getDoorIds())));
              s.sendMessage(component);
            }
          }
          case REMOVE -> {
            if (v == null) {
              s.sendRichMessage("<red>Trigger ID must be specified.");
              return;
            }

            ar.getDoorTriggers().stream()
                .filter(t -> t.getTriggerId().equals(v))
                .findFirst()
                .ifPresentOrElse(
                    trigger -> {
                      ar.getDoorTriggers().remove(trigger);
                      StorageProvider.getArenaStorage().storeArena(ar);
                      s.sendRichMessage("<green>Trigger " + v + " removed.");
                    },
                    () -> s.sendRichMessage("<red>Trigger " + v + " not found."));
          }
        }
      });

  public final Predicate<ArenaSetAction> supports;
  public final QuadConsumer<Arena, ArenaSetAction, @Nullable String, @NotNull CommandSender>
      applyValue;

  private static void sendBoundaries(Arena ar, CommandSender sender) {
    var l = ar.getLowerBound();
    var u = ar.getUpperBound();
    sender.sendMessage(
        Component.text("OK. Arena " + ar.getName() + " boundaries: ", NamedTextColor.GREEN)
            .append(locationComponent(l))
            .appendSpace()
            .append(locationComponent(u)));
  }

  private static Component locationComponent(Location l) {
    return Component.text("[" + l.getX() + ", " + l.getY() + ", " + l.getZ() + "]")
        .clickEvent(ClickEvent.runCommand("/tp " + l.getX() + " " + l.getY() + " " + l.getZ()));
  }

  private static Location unshifted(Location location) {
    var arenaWorld = WorldFactory.getInstance().findByWorld(location.getWorld().getName());
    if (arenaWorld.isEmpty()) return null;
    var original = arenaWorld.get().getOrigin();
    location.setWorld(original.getLowerBound().getWorld());
    return location;
  }
}
