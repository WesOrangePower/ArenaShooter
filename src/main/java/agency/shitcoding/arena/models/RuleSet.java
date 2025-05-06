package agency.shitcoding.arena.models;

import agency.shitcoding.arena.gamestate.*;
import agency.shitcoding.arena.gamestate.team.ETeam;
import agency.shitcoding.arena.gamestate.team.TeamDeathMatchGameFactory;
import agency.shitcoding.arena.gamestate.tutorial.TutorialGameFactory;
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
      new ItemStack(Weapon.GAUNTLET.item),
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
      new ItemStack(Weapon.RAILGUN.item),
      new InstagibGameFactory(),
      new InstagibGameRules()),
  ROF(
      "ruleset.rof",
      false,
      new ItemStack(Weapon.ROCKET_LAUNCHER.item),
      new ROFGameFactory(),
      new ROFGameRules()),
  CTF("ruleset.ctf",
      true,
      new ItemStack(Material.RED_WOOL),
      new CTFGameFactory(),
      new CTFGameRules()),
  TUTORIAL("ruleset.tutorial",
      false,
      new ItemStack(Material.TRIAL_KEY),
      new TutorialGameFactory(),
      new TutorialGameRules());

  private final String name;
  private final boolean teamBased;
  private final ItemStack menuBaseItem;
  private final GameFactory gameFactory;
  private final GameRules defaultGameRules;
}
