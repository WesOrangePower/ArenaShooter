package agency.shitcoding.arena.command;

public class HelpEntry {

  private final String name;
  private final String description;

  public HelpEntry(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getHelpMessage() {
    return "<dark_aqua>– <red>" + name + "<gray> - <dark_aqua>" + description;
  }
}
