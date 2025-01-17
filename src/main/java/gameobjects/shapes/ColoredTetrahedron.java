package main.java.gameobjects.shapes;

import main.java.meth.Vec3;
import main.java.rendering.Renderer;

import java.awt.*;
import main.java.rendering.*;

public class ColoredTetrahedron extends ColoredGameObject
{
    /**
     * Creates a white unit-sized tetrahedron in the origin
     */
    public ColoredTetrahedron() { this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), Color.WHITE); }

    /**
     * Creates a colored tetrahedron with the given position, scale, rotation and color
     */
    public ColoredTetrahedron(Vec3 position, Vec3 scale, Vec3 rotation, Color color)
    {
        super(position, scale, rotation, color);
        this.vertices = Renderer.TetrahedronVertices;
    }
}
