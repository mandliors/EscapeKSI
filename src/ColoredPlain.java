import java.awt.*;

public class ColoredPlain extends ColoredRenderable
{
    public ColoredPlain()
    {
        this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), Color.WHITE);
    }
    public ColoredPlain(Vec3 position, Vec3 scale, Vec3 rotation, Color color)
    {
        translate(position);
        scale(scale);
        rotate(rotation);

        this.vertices = Renderer.PlainVertices;
        this.color = color;
    }

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

            // back-face culling in world space: (quadCenter - camPos) * normal should be positive to be drawn
            Vec3 t1Center = trigon1[0].add(trigon1[1]).add(trigon1[2]).xyz().scale(0.33);
            Vec3 t2Center = trigon2[0].add(trigon2[1]).add(trigon2[2]).xyz().scale(0.33);
            Vec3 quadCenter = t1Center.add(t2Center).scale(0.5);
            Vec3 normal = trigon1[2].xyz().subtract(trigon1[0].xyz()).cross(trigon1[1].xyz().subtract(trigon1[0].xyz())).normalize();
            double dot = (quadCenter.subtract(camera.getPosition())).normalize().dot(normal);
            if (dot <= 0.0)
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

            // discard clipped vertices (triangles)
            if (!Meth.isBetween(trigon1[0].getX(), -1.0, 1.0) || !Meth.isBetween(trigon1[0].getY(), -1.0, 1.0) || !Meth.isBetween(trigon1[0].getZ(), -1.0, 1.0) ||
                    !Meth.isBetween(trigon1[1].getX(), -1.0, 1.0) || !Meth.isBetween(trigon1[1].getY(), -1.0, 1.0) || !Meth.isBetween(trigon1[1].getZ(), -1.0, 1.0) ||
                    !Meth.isBetween(trigon1[2].getX(), -1.0, 1.0) || !Meth.isBetween(trigon1[2].getY(), -1.0, 1.0) || !Meth.isBetween(trigon1[2].getZ(), -1.0, 1.0))
                continue;
            if (!Meth.isBetween(trigon2[0].getX(), -1.0, 1.0) || !Meth.isBetween(trigon2[0].getY(), -1.0, 1.0) || !Meth.isBetween(trigon2[0].getZ(), -1.0, 1.0) ||
                    !Meth.isBetween(trigon2[1].getX(), -1.0, 1.0) || !Meth.isBetween(trigon2[1].getY(), -1.0, 1.0) || !Meth.isBetween(trigon2[1].getZ(), -1.0, 1.0) ||
                    !Meth.isBetween(trigon2[2].getX(), -1.0, 1.0) || !Meth.isBetween(trigon2[2].getY(), -1.0, 1.0) || !Meth.isBetween(trigon2[2].getZ(), -1.0, 1.0))
                continue;

            // clip->screen
            double scaleH = Renderer.getWidth() / 2.0;
            double scaleV = Renderer.getHeight() / 2.0;
            Color col = ColoredRenderable.getShade(color, dot);
            Renderer.drawTriangle(
                    new int[] { (int) (trigon1[0].getX() * scaleH), (int) (trigon1[1].getX() * scaleH), (int) (trigon1[2].getX() * scaleH) },
                    new int[] { (int) (trigon1[0].getY() * scaleV), (int) (trigon1[1].getY() * scaleV), (int) (trigon1[2].getY() * scaleV) },
                    col
            );
            Renderer.drawTriangle(
                    new int[] { (int) (trigon2[0].getX() * scaleH), (int) (trigon2[1].getX() * scaleH), (int) (trigon2[2].getX() * scaleH) },
                    new int[] { (int) (trigon2[0].getY() * scaleV), (int) (trigon2[1].getY() * scaleV), (int) (trigon2[2].getY() * scaleV) },
                    col
            );
        }
    }
}
