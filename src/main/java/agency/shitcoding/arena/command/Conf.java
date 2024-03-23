package agency.shitcoding.arena.command;


public class Conf {

  public static final String arenasSection = "arena.arenas";
  public static final String lobbyLocation = "arena.lobby_location";
  public static final String faqSection = "faqs";

  public static class Arenas {

    public static final String authors = "authors";
    public static final String lowerBound = "lower_bound";
    public static final String upperBound = "upper_bound";
    public static final String lootPointsSection = "loot_points";
    public static final String portalsSection = "portals";
    public static final String rampsSection = "ramps";
    public static final String allowHost = "allow_host";

    public static class LootPoints {

      public static final String location = "location";
      public static final String type = "type";
    }

    public static class Portals {

      public static final String firstLocation = "first_location";
      public static final String secondLocation = "second_location";
      public static final String targetLocation = "target_location";
    }

    public static class Ramps {

      public static final String firstLocation = "first_location";
      public static final String secondLocation = "second_location";
      public static final String multiply = "multiply";
      public static final String vector = "vector";
    }
  }

  public static class Faqs {

    public static final String title = "title";
    public static final String content = "content";
  }
}
