package agency.shitcoding.doublejump.command;

import agency.shitcoding.doublejump.models.Arena;
import agency.shitcoding.doublejump.models.LootPoint;

public class Conf {
    public static final String adminPerm = "arena.admin_perm";
    public static final String arenasSection = "arena.arenas";

    public static class Arenas {
        public static final String name = "arena.arenas.%s";
        public static final String lowerBound = "lower_bound";
        public static final String upperBound = "upper_bound";
        public static final String lootPointsSection = "loot_points";

        public static class LootPoints {
            public static final String lootPointSection = "arena.arenas.%s.loot_points.%s";
            public static final String location = "arena.arenas.%s.loot_points.%s.location";
            public static final String type = "arena.arenas.%s.loot_points.%s.type";
        }
    }

    public static String specify(Arena arena, String path) {
        return String.format(path, arena.getName());
    }
    public static String specify(Arena arena, LootPoint lootPoint, String path) {
        return String.format(path, "LP" + lootPoint.getId());
    }
}
