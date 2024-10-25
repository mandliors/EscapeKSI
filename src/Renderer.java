import java.awt.*;
import java.util.*;
import java.util.List;

public class Renderer
{
    private int width, height;
    private Graphics2D g2d;

    // vertices are in world space (every 3 vertices form a trigon)
    private final List<Vec4> vertices;

    public Renderer(int width, int height, Graphics2D g2d)
    {
        this.width = width;
        this.height = height;
        this.g2d = g2d;

        this.vertices = new ArrayList<Vec4>();
    }

    public void add(Renderable obj)
    {
        for (Trigon t : obj.getTrigons())
            for (Vec3 v : t.getPoints())
                vertices.add(obj.getModel().multiply(new Vec4(v, 1.0)));
    }

    public void render(Mat4 projection, Mat4 view)
    {
        g2d.setColor(new Color(40, 40, 40));
        g2d.fillRect(0, 0, width, height);

        g2d.translate(width / 2, height / 2);
        for (int i = 0; i < vertices.size(); i += 3)
        {
            // world->view
            Vec4[] points = new Vec4[3];
            for (int j = 0; j < 3; j++)
                points[j] = view.multiply(vertices.get(i + j));

            // back-face culling
            Vec3 normal = points[2].xyz().subtract(points[0].xyz()).cross(points[1].xyz().subtract(points[0].xyz()));
            double dot = points[0].xyz().dot(normal);
            if (dot >= 0.0)
                continue;

            g2d.setColor(Color.CYAN);

            // draw normal
            Vec4 start = points[0].add(points[1]).add(points[2]).scale(1.0 / 3.0);
            Vec4 end = start.subtract(new Vec4(normal.normalize().scale(0.2), 1.0));
            start = projection.multiply(start); end = projection.multiply(end);
            start.setX(start.getX() / start.getW()); end.setX(end.getX() / end.getW());
            start.setY(start.getY() / start.getW()); end.setY(end.getY() / end.getW());
            start.setZ(start.getZ() / start.getW()); end.setZ(end.getZ() / end.getW());
            var c = g2d.getColor();
            //g2d.setColor(Color.YELLOW);
            g2d.fillOval((int) (start.getX() * width / 2.0), (int) (start.getY() * height / 2.0), 10, 10);
            g2d.drawLine((int) (start.getX() * width / 2.0), (int) (start.getY() * height / 2.0), (int) (end.getX() * width / 2.0), (int) (end.getY() * height / 2.0));
            g2d.setColor(c);

            // view->clip
            for (int j = 0; j < 3; j++)
            {
                points[j] = projection.multiply(points[j]);
                points[j].setX(points[j].getX() / points[j].getW());
                points[j].setY(points[j].getY() / points[j].getW());
                points[j].setZ(points[j].getZ() / points[j].getW());
            }

            // clip->screen
            double scaleH = width / 2.0;
            double scaleV = height / 2.0;
            g2d.drawPolygon(
                    new int[]{(int) (points[0].getX() * scaleH), (int) (points[1].getX() * scaleH), (int) (points[2].getX() * scaleH)},
                    new int[]{(int) (points[0].getY() * scaleV), (int) (points[1].getY() * scaleV), (int) (points[2].getY() * scaleV)}, 3
            );
        }
        g2d.translate(-width / 2, -height / 2);

        vertices.clear();
    }
}
