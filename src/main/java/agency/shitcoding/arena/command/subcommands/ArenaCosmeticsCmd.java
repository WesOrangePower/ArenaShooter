package agency.shitcoding.arena.command.subcommands;

import static io.vavr.control.Validation.invalid;
import static io.vavr.control.Validation.valid;
import static net.kyori.adventure.text.Component.text;

import agency.shitcoding.arena.command.ArenaCommand;
import agency.shitcoding.arena.command.CommandInst;
import agency.shitcoding.arena.gamestate.CosmeticsService;
import agency.shitcoding.arena.gamestate.WeaponMod;
import agency.shitcoding.arena.gamestate.WeaponMods;
import agency.shitcoding.arena.suggester.Suggester;
import agency.shitcoding.arena.suggester.SuggesterBuilder;
import io.vavr.control.Validation;
import java.util.Arrays;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class ArenaCosmeticsCmd extends CommandInst {

  public ArenaCosmeticsCmd(CommandSender sender, String[] args) {
    super(sender, args);
  }

  public static final Suggester SUGGESTER =
      SuggesterBuilder.builder()
          .at(2)
          .suggestEnumLower(() -> CosmeticsOperation.Type.class)
          .at(3)
          .inCaseArgIsIgnoreCase(1, "list")
          .suggestPlayers()
          .at(3)
          .inCaseArgIsNotIgnoreCase(1, "list")
          .suggest(() -> Arrays.stream(WeaponMods.REGISTRY).map(WeaponMod::mod).toList())
          .at(4)
          .inCaseArgIsNotIgnoreCase(1, "list")
          .suggestPlayers()
          .build();

  @Override
  public void execute() {
    Validation<Component, CosmeticsOperation> validation = validate();
    if (validation.isInvalid()) {
      sender.sendMessage(validation.getError());
    } else {
      execute(validation.get());
    }
  }

  // /arena cosmetics <list|add|remove> [weaponMod @arg0 add|remove] [player ? self]

  private void execute(CosmeticsOperation op) {
    CosmeticsService cosmetics = CosmeticsService.getInstance();
    switch (op.type) {
      case LIST -> {
        assert op.target != null;
        var mods = cosmetics.getAllAvailableWeaponMods(op.target);
        if (mods.isEmpty()) {
          sender.sendMessage(translateRich("command.cosmetics.noMods"));
        } else {
          Component message =
              mods.stream()
                  .map(
                      mod ->
                          translateRich(mod.weapon().translatableName)
                              .append(text(": ", NamedTextColor.GREEN))
                              .append(translateRich(mod.getTranslationKey())))
                  .reduce(
                      translateRich("command.cosmetics.list", op.target),
                      (a, b) -> a.appendNewline().append(b),
                      (a, b) -> a.appendNewline().append(b));
          sender.sendMessage(message);
        }
      }
      case ADD -> {
        assert op.mod != null;
        assert op.target != null;
        cosmetics.addWeaponMod(op.target, op.mod);
        sender.sendMessage(
            translateRich(
                "command.cosmetics.added", translatePlain(op.mod.getTranslationKey()), op.target));
      }
      case REMOVE -> {
        assert op.mod != null;
        assert op.target != null;
        cosmetics.removeWeaponMod(op.target, op.mod);
        sender.sendMessage(
            translateRich(
                "command.cosmetics.removed",
                translatePlain(op.mod.getTranslationKey()),
                op.target));
      }
    }
  }

  private Validation<Component, CosmeticsOperation> validate() {
    String adminPerm = ArenaCommand.getAdminPerm();
    if (!sender.hasPermission(adminPerm)) {
      return invalid(translateRich("command.noPermission"));
    }

    CosmeticsOperation op = new CosmeticsOperation();
    try {
      String opType = argOrNull(1);
      if (opType == null) {
        return invalid(usage());
      }
      op.type = Enum.valueOf(CosmeticsOperation.Type.class, opType.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      return invalid(usage());
    }

    if (op.type == CosmeticsOperation.Type.LIST) {
      op.target = argOrNull(2);
    } else {
      op.target = argOrNull(3);
    }

    if (op.target == null) {
      if (!(sender instanceof Player player)) {
        return invalid(usage());
      }
      op.target = player.getName();
    }

    if (op.type == CosmeticsOperation.Type.LIST) {
      return valid(op);
    }

    if (args.length < 3) {
      return invalid(usage());
    }

    String weaponModName = argOrNull(2);
    if (weaponModName == null) {
      return invalid(usage());
    }
    WeaponMod foundMod = WeaponMods.findByName(weaponModName);
    if (foundMod == null) {
      return invalid(translateRich("command.cosmetics.unknownMod", weaponModName));
    }

    op.mod = foundMod;
    return valid(op);
  }

  private Component usage() {
    Component message =
        translateRich("command.cosmetics.usage")
            .appendNewline()
            .append(translateRich("command.cosmetics.available"));
    for (int i = 0; i < WeaponMods.REGISTRY.length; i++) {
      WeaponMod weaponMod = WeaponMods.REGISTRY[i];
      if (i != 0) {
        message = message.append(text(", "));
      }
      message = message.append(text(weaponMod.mod()));
    }
    return message;
  }

  private static class CosmeticsOperation {
    private @Nullable String target = "";
    private Type type = Type.LIST;
    private @Nullable WeaponMod mod;

    private enum Type {
      LIST,
      ADD,
      REMOVE
    }
  }
}
