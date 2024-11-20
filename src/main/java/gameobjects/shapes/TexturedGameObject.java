package main.java.gameobjects.shapes;

import main.java.assets.*;

import main.java.assets.AssetManager;
import main.java.gameobjects.GameObject;
import main.java.meth.Mat4;
import main.java.meth.Vec3;
import main.java.meth.Vec4;
import main.java.rendering.Camera;
import main.java.rendering.Renderer;

import main.java.rendering.*;

public abstract class TexturedGameObject extends GameObject
{
    protected double[] vertices;
    protected String textureName;

    public TexturedGameObject(Vec3 position, Vec3 scale, Vec3 rotation, String textureName)
    {
        translate(position);
        scale(scale);
        rotate(rotation);

        this.vertices = null;
        this.textureName = textureName;
    }

    public void update(double dt) { }
    public void render(Camera camera)
    {
        Mat4 viewProj = camera.getProjectionMatrix().multiply(camera.getViewMatrix());
        Mat4 model = getModelMatrix();

        for (int i = 0; i < vertices.length; i += 3 * 5)
        {
            // model->world
            Vec4[] trigon = new Vec4[3];
            double[] u = new double[3];
            double[] v = new double[3];
            for (int j = 0; j < 3; j++)
            {
                Vec4 vertex1 = new Vec4(
                        vertices[j * 5 + i + 0],
                        vertices[j * 5 + i + 1],
                        vertices[j * 5 + i + 2],
                        1.0
                );
                trigon[j] = model.multiply(vertex1);
                u[j] = vertices[j * 5 + i + 3];
                v[j] = vertices[j * 5 + i + 4];
            }

            // back-face culling in world space: (quadCenter - camPos) * normal should be positive to be drawn
            Vec3 center = trigon[0].add(trigon[1]).add(trigon[2]).xyz().scale(0.33);
            Vec3 normal = trigon[2].xyz().subtract(trigon[0].xyz()).cross(trigon[1].xyz().subtract(trigon[0].xyz())).normalize();
            double dot = (center.subtract(camera.getPosition())).normalize().dot(normal);
            if (dot <= 0.0)
                continue;

            // world->view->clip
            for (int j = 0; j < 3; j++)
            {
                trigon[j] = viewProj.multiply(trigon[j]);
                trigon[j].setX(trigon[j].getX() / trigon[j].getW());
                trigon[j].setY(trigon[j].getY() / trigon[j].getW());
                trigon[j].setZ(trigon[j].getZ() / trigon[j].getW());
            }

            // clip->screen
            Renderer.drawTexturedTriangle(
                    AssetManager.getTexture(textureName).get(),
                    new Vec3[] { trigon[0].xyz(), trigon[1].xyz(), trigon[2].xyz() },
                    u,
                    v
            );
        }
    }
}
