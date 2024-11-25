package main.java.gameobjects.shapes;

import main.java.meth.Vec3;
import main.java.rendering.Renderer;

import main.java.rendering.*;

public class TexturedPlain extends TexturedGameObject
{
    /**
     * Creates a unit-sized textured plain in the origin with the placeholder texture
     */
    public TexturedPlain()
    {
        this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), "placeholder");
    }

    /**
     * Creates a textured plain with the given position, scale, rotation and texture name
     */
    public TexturedPlain(Vec3 position, Vec3 scale, Vec3 rotation, String textureName)
    {
        super(position, scale, rotation, textureName);
        this.vertices = Renderer.PlainVertices;
    }
}
