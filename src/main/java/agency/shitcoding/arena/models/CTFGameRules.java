package agency.shitcoding.arena.models;


public class CTFGameRules extends TDMGameRules implements GameRules {
  @Override
  public boolean showHealth() {
    return false;
  }
}
