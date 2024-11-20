package main.java.gameobjects.shapes;

import main.java.meth.Vec3;
import main.java.rendering.Renderer;

import main.java.rendering.*;

public class TexturedCube extends TexturedGameObject
{
    public TexturedCube() { this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), "placeholder"); }
    public TexturedCube(Vec3 position, Vec3 scale, Vec3 rotation, String textureName)
    {
        super(position, scale, rotation, textureName);
        this.vertices = Renderer.CubeVertices;
    }
}
