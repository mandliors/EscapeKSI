import java.awt.*;
import java.awt.image.BufferedImage;

public class TexturedCube extends TexturedRenderable
{
    public TexturedCube() { this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), "placeholder"); }
    public TexturedCube(Vec3 position, Vec3 scale, Vec3 rotation, String textureName)
    {
        super(position, scale, rotation, textureName);
        this.vertices = Renderer.CubeVertices;
    }
}
