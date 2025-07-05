package agency.shitcoding.arena.models;

import agency.shitcoding.arena.gamestate.*;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamDeathMatchGameFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public enum RuleSet {
  DM(
      "ruleset.dm",
      false,
      Weapon.GAUNTLET.generateItem(),
      new DeathMatchGameFactory(),
      new DMGameRules()),
  TDM(
      "ruleset.tdm",
      true,
      ETeam.BLUE.getTeamMeta().getHelmet(),
      new TeamDeathMatchGameFactory(),
      new TDMGameRules()),
  LMS(
      "ruleset.lms",
      false,
      new ItemStack(Material.GOLDEN_APPLE),
      new LMSGameFactory(),
      new LMSGameRules()),
  INSTAGIB(
      "ruleset.instagib",
      false,
      Weapon.RAILGUN.generateItem(),
      new InstagibGameFactory(),
      new InstagibGameRules()),
  ROF(
      "ruleset.rof",
      false,
      Weapon.ROCKET_LAUNCHER.generateItem(),
      new ROFGameFactory(),
      new ROFGameRules()),
  CTF("ruleset.ctf",
      true,
      new ItemStack(ETeam.RED.getIcon()),
      new CTFGameFactory(),
      new CTFGameRules());

  private final String name;
  private final boolean teamBased;
  private final ItemStack menuBaseItem;
  private final GameFactory gameFactory;
  private final GameRules defaultGameRules;
}
