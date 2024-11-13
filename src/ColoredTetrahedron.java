import java.awt.*;

public class ColoredTetrahedron extends ColoredRenderable
{
    public ColoredTetrahedron() { this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), Color.WHITE); }
    public ColoredTetrahedron(Vec3 position, Vec3 scale, Vec3 rotation, Color color)
    {
        translate(position);
        scale(scale);
        rotate(rotation);

        this.vertices = Renderer.TetrahedronVertices;
        this.color = color;
    }
}
