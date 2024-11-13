import java.awt.*;

public abstract class ColoredRenderable extends Renderable
{
    protected double[] vertices;
    protected Color color;

    public void render(Camera camera)
    {
        Mat4 viewProj = camera.getProjectionMatrix().multiply(camera.getViewMatrix());
        Mat4 model = getModelMatrix();

        for (int i = 0; i < vertices.length; i += 3 * 5)
        {
            // model->world
            Vec4[] trigon = new Vec4[3];
            for (int j = 0; j < 3; j++)
            {
                Vec4 vertex1 = new Vec4(
                        vertices[j * 5 + i + 0],
                        vertices[j * 5 + i + 1],
                        vertices[j * 5 + i + 2],
                        1.0
                );
                trigon[j] = model.multiply(vertex1);
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

            // discard clipped vertices (triangles)
            if (!Meth.isBetween(trigon[0].getX(), -1.0, 1.0) || !Meth.isBetween(trigon[0].getY(), -1.0, 1.0) || !Meth.isBetween(trigon[0].getZ(), -1.0, 1.0) ||
                    !Meth.isBetween(trigon[1].getX(), -1.0, 1.0) || !Meth.isBetween(trigon[1].getY(), -1.0, 1.0) || !Meth.isBetween(trigon[1].getZ(), -1.0, 1.0) ||
                    !Meth.isBetween(trigon[2].getX(), -1.0, 1.0) || !Meth.isBetween(trigon[2].getY(), -1.0, 1.0) || !Meth.isBetween(trigon[2].getZ(), -1.0, 1.0))
                continue;

            // clip->screen
            double scaleH = Renderer.getWidth() / 2.0;
            double scaleV = Renderer.getHeight() / 2.0;
            Renderer.drawTriangle(
                    new int[] { (int) (trigon[0].getX() * scaleH), (int) (trigon[1].getX() * scaleH), (int) (trigon[2].getX() * scaleH) },
                    new int[] { (int) (trigon[0].getY() * scaleV), (int) (trigon[1].getY() * scaleV), (int) (trigon[2].getY() * scaleV) },
                    ColoredRenderable.getShade(color, dot)
            );
        }
    }

    public static Color getShade(Color color, double shade)
    {
        //source: https://www.alibabacloud.com/blog/construct-a-simple-3d-rendering-engine-with-java_599599

        double redLinear = Math.pow(color.getRed(), 2.2) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.2) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.2) * shade;

        int red = (int) Math.pow(redLinear, 1 / 2.2);
        int green = (int) Math.pow(greenLinear, 1 / 2.2);
        int blue = (int) Math.pow(blueLinear, 1 / 2.2);

        return new Color(red, green, blue);
    }
}
