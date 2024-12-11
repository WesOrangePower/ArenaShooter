package agency.shitcoding.arena.util;


import java.util.ArrayList;
import java.util.List;

public class HelixUtil {
  public static final double DT = .1d;
  public static List<double[]> helixAroundLine(
      double sx,
      double sy,
      double sz,
      double ex,
      double ey,
      double ez,
      double dirX,
      double dirY,
      double dirZ,
      float radius,
      float turns) {
    List<double[]> points = new ArrayList<>();
    boolean
        longestAxisX =
        Math.abs(ex - sx) > Math.abs(ey - sy) && Math.abs(ex - sx) > Math.abs(ez - sz),
        longestAxisY =
            Math.abs(ey - sy) > Math.abs(ex - sx) && Math.abs(ey - sy) > Math.abs(ez - sz),
        longestAxisZ =
            Math.abs(ez - sz) > Math.abs(ex - sx) && Math.abs(ez - sz) > Math.abs(ey - sy);

    double lineLength = longestAxisX
        ? Math.abs(ex - sx)
        : longestAxisY
            ? Math.abs(ey - sy)
            : Math.abs(ez - sz);

    int iterations = (int) (lineLength / DT);

    for (int i = 0; i <= iterations; i++) {
      double t = i * DT;
      // line point
      double lx = dirX * t + sx;
      double ly = dirY * t + sy;
      double lz = dirZ * t + sz;
      // helix point
      double x = radius * Math.cos(2 * Math.PI * turns * t);
      double y = radius * Math.sin(2 * Math.PI * turns * t);
      @SuppressWarnings("UnnecessaryLocalVariable")
      double z = t;
      // rotate helix point
      double a = Math.atan2(dirY, dirX);
      double b = Math.acos(dirZ);
      double cosA = Math.cos(a);
      double sinA = Math.sin(a);
      double cosB = Math.cos(b);
      double sinB = Math.sin(b);
      double rotatedX = x * (cosA * cosB) + y * (-sinA) + z * (cosA * sinB);
      double rotatedY = x * (sinA * cosB) + y * (cosA) + z * (sinA * sinB);
      double rotatedZ = x * (-sinB) + y * (0) + z * (cosB);
      // world helix point
      double hx = rotatedX + lx;
      double hy = rotatedY + ly;
      double hz = rotatedZ + lz;

      // add only if point does not exceed end point
      if ((longestAxisX && Math.abs(hx - sx) < Math.abs(ex - sx))
          || (longestAxisY && Math.abs(hy - sy) < Math.abs(ey - sy))
          || (longestAxisZ && Math.abs(hz - sz) < Math.abs(ez - sz))) {
        points.add(new double[] {hx, hy, hz});
      }
    }

    return points;
  }
}
