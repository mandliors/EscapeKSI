package main.java.gameobjects.shapes;

import main.java.meth.Mat4;
import main.java.meth.Vec3;
import main.java.meth.Vec4;
import main.java.rendering.Camera;
import main.java.rendering.Renderer;

import java.awt.*;
import main.java.rendering.*;

public class ColoredCube extends ColoredGameObject
{
    /**
     * Creates a white unit-sized cube in the origin
     */
    public ColoredCube()
    {
        this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), Color.WHITE);
    }

    /**
     * Creates a colored cube with the given position, scale rotation and color
     */
    public ColoredCube(Vec3 position, Vec3 scale, Vec3 rotation, Color color)
    {
        super(position, scale, rotation, color);
        this.vertices = Renderer.CubeVertices;
    }

    public void update(double dt) { }

    /**
     * Renders the cube from the camera's point of view (faces are rendered in one call, not using the default algorithm where they would be split up into two triangles)
     * This is important for shading
     * @param camera From what pov the cube will be rendered
     */
    public void render(Camera camera)
    {
        Mat4 viewProj = camera.getProjectionMatrix().multiply(camera.getViewMatrix());
        Mat4 model = getModelMatrix();

        for (int i = 0; i < vertices.length; i += 3 * 5 * 2)
        {
            // model->world
            Vec4[] trigon1 = new Vec4[3];
            Vec4[] trigon2 = new Vec4[3];
            for (int j = 0; j < 3; j++)
            {
                Vec4 vertex1 = new Vec4(
                        vertices[j * 5 + i + 0],
                        vertices[j * 5 + i + 1],
                        vertices[j * 5 + i + 2],
                        1.0
                );
                Vec4 vertex2 = new Vec4(
                        vertices[(j + 3) * 5 + i + 0],
                        vertices[(j + 3) * 5 + i + 1],
                        vertices[(j + 3) * 5 + i + 2],
                        1.0
                );

                trigon1[j] = model.multiply(vertex1);
                trigon2[j] = model.multiply(vertex2);
            }

            // shading and back-face culling in world space: (quadCenter - camPos) * normal should be positive to be drawn
            Vec3 quadCenter = trigon1[0].add(trigon1[1]).add(trigon1[2]).add(trigon2[1]).xyz().scale(0.25);
            Vec3 normal = trigon1[2].xyz().subtract(trigon1[0].xyz()).cross(trigon1[1].xyz().subtract(trigon1[0].xyz())).normalize();
            double dot = (quadCenter.subtract(camera.getPosition())).normalize().dot(normal);
            if (Renderer.isBackfaceCullingEnabled() && dot <= 0.0)
                continue;

            // world->view->clip
            for (int j = 0; j < 3; j++)
            {
                trigon1[j] = viewProj.multiply(trigon1[j]);
                trigon1[j].setX(trigon1[j].getX() / trigon1[j].getW());
                trigon1[j].setY(trigon1[j].getY() / trigon1[j].getW());
                trigon1[j].setZ(trigon1[j].getZ() / trigon1[j].getW());

                trigon2[j] = viewProj.multiply(trigon2[j]);
                trigon2[j].setX(trigon2[j].getX() / trigon2[j].getW());
                trigon2[j].setY(trigon2[j].getY() / trigon2[j].getW());
                trigon2[j].setZ(trigon2[j].getZ() / trigon2[j].getW());
            }

            // clip->screen
            Renderer.drawQuad(
                    new Vec3[] { trigon1[0].xyz(), trigon1[1].xyz(), trigon1[2].xyz() },
                    new Vec3[] { trigon2[0].xyz(), trigon2[1].xyz(), trigon2[2].xyz() },
                    ColoredGameObject.getShade(color, dot)
            );
        }
    }
}
