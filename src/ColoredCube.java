import java.awt.*;

public class ColoredCube extends ColoredObject
{
    public ColoredCube()
    {
        this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), Color.WHITE);
    }
    public ColoredCube(Vec3 position, Vec3 scale, Vec3 rotation, Color color)
    {
        translate(position);
        scale(scale);
        rotate(rotation);

        this.data = Renderer.ColoredCubeData;
        this.color = color;
    }
}
