import java.awt.*;
import java.awt.image.BufferedImage;

public class TexturedTetrahedron extends TexturedRenderable
{
    public TexturedTetrahedron() { this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), "placeholder"); }
    public TexturedTetrahedron(Vec3 position, Vec3 scale, Vec3 rotation, String textureName)
    {
        super(position, scale, rotation, textureName);
        this.vertices = Renderer.TetrahedronVertices;
    }
}
