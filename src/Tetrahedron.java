import java.awt.*;

public class Tetrahedron extends ColoredObject
{
    public Tetrahedron() { this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), Color.WHITE); }
    public Tetrahedron(Vec3 position, Vec3 scale, Vec3 rotation, Color color)
    {
        translate(position);
        scale(scale);
        rotate(rotation);

        this.data = Renderer.ColoredTetrahedronData;
        this.color = color;
    }
}
