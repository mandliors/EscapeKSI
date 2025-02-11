package main.java.gameobjects.shapes;

import main.java.meth.Vec3;
import main.java.rendering.Renderer;

import main.java.rendering.*;

public class TexturedTetrahedron extends TexturedGameObject
{
    /**
     * Creates a unit-sized textured tetrahedron in the origin with the placeholder texture
     */
    public TexturedTetrahedron() { this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), "placeholder"); }

    /**
     * Creates a tetrahedron with the given position, scale, rotation and texture name
     */
    public TexturedTetrahedron(Vec3 position, Vec3 scale, Vec3 rotation, String textureName)
    {
        super(position, scale, rotation, textureName);
        this.vertices = Renderer.TetrahedronVertices;
    }
}
