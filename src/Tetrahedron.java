import java.awt.*;

public class Tetrahedron extends Renderable
{
    private double[] vertices;
    private short[] indices;

    Color color;

    public Tetrahedron()
    {
        this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0), Color.WHITE);
    }
    public Tetrahedron(Vec3 position, Vec3 scale, Vec3 rotation, Color color)
    {
        translate(position);
        scale(scale);
        rotate(rotation);

        this.color = color;

        this.vertices = new double[] {
                // face 1
                -0.5,  0.5, -0.5,
                -0.5, -0.5,  0.5,
                 0.5,  0.5,  0.5,
                // face 2
                 0.5,  0.5,  0.5,
                -0.5, -0.5,  0.5,
                 0.5, -0.5, -0.5,
                // face 3
                 0.5,  0.5,  0.5,
                 0.5, -0.5, -0.5,
                -0.5,  0.5, -0.5,
                // face 4
                -0.5,  0.5, -0.5,
                 0.5, -0.5, -0.5,
                -0.5, -0.5,  0.5
        };
    }

    public void render(Camera camera)
    {
        Mat4 viewProj = camera.getProjectionMatrix().multiply(camera.getViewMatrix());

        for (int i = 0; i < vertices.length; i += 9)
        {
            // model->world
            Vec4[] points = new Vec4[3];
            for (int j = 0; j < 3; j++)
            {
                Vec4 vertex = new Vec4(0.0);
                vertex.setX(vertices[j * 3 + i + 0]);
                vertex.setY(vertices[j * 3 + i + 1]);
                vertex.setZ(vertices[j * 3 + i + 2]);
                vertex.setW(1.0);

                points[j] = model.multiply(vertex);
            }

            // back-face culling in world space: (vertexPos - camPos) * normal should be positive to be drawn
            Vec3 normal = points[2].xyz().subtract(points[0].xyz()).cross(points[1].xyz().subtract(points[0].xyz())).normalize();
            double dot = (points[0].xyz().subtract(camera.getPosition())).normalize().dot(normal);
            if (dot <= 0.0)
                continue;

            // calculuate normal (points have to be in view space
//                Vec4 start = points[0].add(points[1]).add(points[2]).scale(1.0 / 3.0);
//                Vec4 end = start.subtract(new Vec4(normal.normalize().scale(0.2), 1.0));
//                start = cam.getProjection().multiply(start); end = cam.getProjection().multiply(end);
//                start.setX(start.getX() / start.getW()); end.setX(end.getX() / end.getW());
//                start.setY(start.getY() / start.getW()); end.setY(end.getY() / end.getW());
//                start.setZ(start.getZ() / start.getW()); end.setZ(end.getZ() / end.getW());

            // world->view->clip
            for (int j = 0; j < 3; j++)
            {
                points[j] = viewProj.multiply(points[j]);
                points[j].setX(points[j].getX() / points[j].getW());
                points[j].setY(points[j].getY() / points[j].getW());
                points[j].setZ(points[j].getZ() / points[j].getW());
            }

            // clip->screen
            double scaleH = Renderer.getWidth() / 2.0;
            double scaleV = Renderer.getHeight() / 2.0;
            int[] xCoords = new int[]{(int) (points[0].getX() * scaleH), (int) (points[1].getX() * scaleH), (int) (points[2].getX() * scaleH)};
            int[] yCoords = new int[]{(int) (points[0].getY() * scaleV), (int) (points[1].getY() * scaleV), (int) (points[2].getY() * scaleV)};
            Renderer.drawTriangle(xCoords, yCoords, Renderer.getShade(color, dot));
        }
    }

    public double[] getVertices() { return vertices; }
    public short[] getIndices() { return indices; }
}
