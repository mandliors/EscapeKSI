import java.awt.*;

public class Trigon
{
    private Vec3[] points = new Vec3[3];
    private Color color;

    private Mat4 model = new Mat4(1.0);

    public Trigon(Vec3 p1, Vec3 p2, Vec3 p3, Color color)
    {
        points[0] = p1;
        points[1] = p2;
        points[2] = p3;

        this.color = color;

        model = model.mul(Meth.translate(new Vec3(0, 0, 10.2)));
        model = model.mul(Meth.scale(new Vec3(0.5)));
        model = model.mul(Meth.rotate(new Vec3(1, 0.2, 0.5), 45.0));
    }

    public Vec3[] getPoints() { return points; }
    public Color getColor() { return color; }

    public Mat4 getModelMatrix() { return model; }
}
