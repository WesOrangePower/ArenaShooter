package agency.shitcoding.doublejump.events;

public class MajorBuffTracker {
    private static Integer quadDamageTicks = null;
    private static Integer protectionTicks = null;

    public static Integer getProtectionTicks() {
        return protectionTicks;
    }

    public static void setProtectionTicks(Integer protectionTicks) {
        MajorBuffTracker.protectionTicks = protectionTicks;
    }

    public static Integer getQuadDamageTicks() {
        return quadDamageTicks;
    }

    public static void setQuadDamageTicks(Integer quadDamageTicks) {
        MajorBuffTracker.quadDamageTicks = quadDamageTicks;
    }
}
