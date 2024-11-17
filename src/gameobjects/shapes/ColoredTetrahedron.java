package gameobjects.shapes;

import java.awt.*;
import math.*;
import rendering.*;

public class ColoredTetrahedron extends ColoredGameObject
{
    public ColoredTetrahedron() { this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), Color.WHITE); }
    public ColoredTetrahedron(Vec3 position, Vec3 scale, Vec3 rotation, Color color)
    {
        super(position, scale, rotation, color);
        this.vertices = Renderer.TetrahedronVertices;
    }
}
