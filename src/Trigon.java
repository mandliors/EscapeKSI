import java.awt.*;

public class Trigon
{
    Vec3 p1;
    Vec3 p2;
    Vec3 p3;
    Color color;

    public Trigon(Vec3 p1, Vec3 p2, Vec3 p3, Color color)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.color = color;
    }

    public Vec3 getP1() { return p1; }
    public Vec3 getP2() { return p2; }
    public Vec3 getP3() { return p3; }

    public Color getColor() { return color; }
}
