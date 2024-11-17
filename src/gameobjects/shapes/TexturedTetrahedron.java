package gameobjects.shapes;

import rendering.*;
import math.*;

public class TexturedTetrahedron extends TexturedGameObject
{
    public TexturedTetrahedron() { this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), "placeholder"); }
    public TexturedTetrahedron(Vec3 position, Vec3 scale, Vec3 rotation, String textureName)
    {
        super(position, scale, rotation, textureName);
        this.vertices = Renderer.TetrahedronVertices;
    }
}
