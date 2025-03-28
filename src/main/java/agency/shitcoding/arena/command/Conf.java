package agency.shitcoding.arena.command;


public final class Conf {

  public static final String arenasSection = "arena.arenas";
  public static final String lobbyLocation = "arena.lobby_location";
  public static final String faqSection = "faqs";
  public static final String weaponModSection = "weapon_mods";

  public static class Arenas {

    public static final String authors = "authors";
    public static final String lowerBound = "lower_bound";
    public static final String upperBound = "upper_bound";
    public static final String lootPointsSection = "loot_points";
    public static final String portalsSection = "portals";
    public static final String windTunnelsSection = "wind_tunnels";
    public static final String rampsSection = "ramps";
    public static final String doorsSection = "doors";
    public static final String doorTriggersSection = "door_triggers";
    public static final String allowHost = "allow_host";
    public static final String tags = "tags";
    public static final String supportedRuleSets = "supported_rulesets";

    public static class LootPoints {

      public static final String location = "location";
      public static final String type = "type";
      public static final String isSpawnPoint = "is_spawn_point";
      public static final String markers = "markers";

      private LootPoints() {}
    }

    public static class Portals {

      public static final String firstLocation = "first_location";
      public static final String secondLocation = "second_location";
      public static final String targetLocation = "target_location";

      private Portals() {}
    }

    public static class WindTunnels {

      public static final String id = "id";
      public static final String firstCorner = "first_corner";
      public static final String secondCorner = "second_corner";
      public static final String velocity = "velocity";

      private WindTunnels() {}
    }

    public static class Ramps {

      public static final String firstLocation = "first_location";
      public static final String secondLocation = "second_location";
      public static final String multiply = "multiply";
      public static final String vector = "vector";

      private Ramps() {}
    }

    public static class Doors {
      public static final String firstLocation = "first_location";
      public static final String secondLocation = "second_location";
      public static final String destinationCenter = "destination_center";
      public static final String doorType = "door_type";
      public static final String animationTime = "animation_time";
      public static final String closeAfterTicks = "close_after_ticks";
      public static final String replaceAir = "replace_air";

      private Doors() {}
    }

    public static class DoorTriggers {
      public static final String triggerType = "trigger_type";
      public static final String location = "location";
      public static final String doorIds = "door_ids";

      private DoorTriggers() {}
    }
  }

  public static class Faqs {

    public static final String title = "title";
    public static final String content = "content";

    private Faqs() {}
  }

  private Conf() {}
}
