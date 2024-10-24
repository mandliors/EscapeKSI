import java.awt.*;

public class Trigon
{
    private Vec3[] points;

    public Trigon(Vec3 p1, Vec3 p2, Vec3 p3)
    {
        this.points = new Vec3[3];
        this.points[0] = p1;
        this.points[1] = p2;
        this.points[2] = p3;
    }

    public Vec3[] getPoints() { return points; }
}
