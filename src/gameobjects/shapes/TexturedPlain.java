package gameobjects.shapes;

import math.*;
import rendering.*;

public class TexturedPlain extends TexturedGameObject
{
    public TexturedPlain()
    {
        this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), "placeholder");
    }
    public TexturedPlain(Vec3 position, Vec3 scale, Vec3 rotation, String textureName)
    {
        super(position, scale, rotation, textureName);
        this.vertices = Renderer.PlainVertices;
    }
}
