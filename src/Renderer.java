import javax.imageio.ImageIO;
import javax.xml.crypto.dsig.Transform;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Renderer
{
    // target canvas for the rendering
    private static Canvas canvas;
    private static int width, height;

    private static final List<Renderable> objects = new ArrayList<>();
    private static BufferedImage ksi = null;

    public static void init(Canvas canvas)
    {
        try { ksi = ImageIO.read(new File("res/ksi.png")); }
        catch (IOException e) { ksi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); }

        Renderer.canvas = canvas;

        Renderer.width = canvas.getWidth();
        Renderer.height = canvas.getHeight();
    }

    public static void add(Renderable obj)
    {
        objects.add(obj);
    }

    public static void render(Camera cam)
    {
        Graphics2D g2d = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();

        g2d.setColor(new Color(40, 40, 40));
        g2d.fillRect(0, 0, width, height);

        g2d.translate(width / 2, height / 2);
        g2d.scale(1.0, -1.0);
        for (Renderable obj : objects)
        {
            Mat4 viewProj = cam.getProjection().multiply(cam.getView());

            for (int i = 0; i < obj.getVertices().length; i += 9)
            {
                // model->world
                Vec4[] points = new Vec4[3];
                for (int j = 0; j < 3; j++)
                {
                    Vec4 vertex = new Vec4(0.0);
                    vertex.setX(obj.getVertices()[j * 3 + i + 0]);
                    vertex.setY(obj.getVertices()[j * 3 + i + 1]);
                    vertex.setZ(obj.getVertices()[j * 3 + i + 2]);
                    vertex.setW(1.0);

                    points[j] = obj.getModel().multiply(vertex);
                }

                // back-face culling in world space: (vertexPos - camPos) * normal should be positive to be drawn
                Vec3 normal = points[2].xyz().subtract(points[0].xyz()).cross(points[1].xyz().subtract(points[0].xyz())).normalize();
                double dot = (points[0].xyz().subtract(cam.getPosition())).normalize().dot(normal);
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
                double scaleH = width / 2.0;
                double scaleV = height / 2.0;
                int[] xCoords = new int[] { (int) (points[0].getX() * scaleH), (int) (points[1].getX() * scaleH), (int) (points[2].getX() * scaleH)};
                int[] yCoords = new int[] { (int) (points[0].getY() * scaleV), (int) (points[1].getY() * scaleV), (int) (points[2].getY() * scaleV)};

                // shading
                g2d.setColor(getShade(Color.CYAN, dot));
                if (i == 45)
                {
                    double[] u = new double[] { 0.0, 1.0, 1.0 };
                    double[] v = new double[] { 1.0, 1.0, 0.0 };
                    drawTexturedTriangle(ksi, xCoords, yCoords, u, v);
                }
                else if (i == 36)
                {
                    double[] u = new double[] { 1.0, 0.0, 0.0 };
                    double[] v = new double[] { 0.0, 0.0, 1.0 };
                    drawTexturedTriangle(ksi, xCoords, yCoords, u, v);
                }
                else
                    g2d.fillPolygon(xCoords, yCoords, 3);

                // draw normal
//                g2d.setColor(Color.YELLOW);
//                g2d.fillOval((int) (start.getX() * width / 2.0) - 5, (int) (start.getY() * height / 2.0) - 5, 10, 10);
//                g2d.drawLine((int) (start.getX() * width / 2.0), (int) (start.getY() * height / 2.0), (int) (end.getX() * width / 2.0), (int) (end.getY() * height / 2.0));
            }
        }

        objects.clear();
    }

    private static void drawTexturedTriangle(BufferedImage texture, int[] xCoords, int[] yCoords, double[] u, double[] v)
    {
        //source: chatgpt

        Graphics2D g2d = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
        g2d.translate(width / 2, height / 2);
        g2d.scale(1.0, -1.0);

        // calculate bounding box
        int minX = Math.min(xCoords[0], Math.min(xCoords[1], xCoords[2]));
        int minY = Math.min(yCoords[0], Math.min(yCoords[1], yCoords[2]));
        int maxX = Math.max(xCoords[0], Math.max(xCoords[1], xCoords[2]));
        int maxY = Math.max(yCoords[0], Math.max(yCoords[1], yCoords[2]));

        // iterate through the bounding box
        for (int y = minY; y <= maxY; y++)
        {
            for (int x = minX; x <= maxX; x++)
            {
                // draw pixels that are inside the triangle
                if (isInsideTriangle(x, y, xCoords, yCoords))
                {
                    double[] uv = interpolateUV(x, y, xCoords, yCoords, u, v);
                    int texX = (int) (uv[0] * texture.getWidth());
                    int texY = (int) (uv[1] * texture.getHeight());

                    texX = Math.clamp(texX, 0, texture.getWidth() - 1);
                    texY = Math.clamp(texY, 0, texture.getHeight() - 1);

                    int color = texture.getRGB(texX, texY);
                    g2d.setColor(new Color(color));
                    g2d.drawLine(x, y, x, y);
                }
            }
        }
    }


    private static double[] interpolateUV(int x, int y, int[] xPoints, int[] yPoints, double[] u, double[] v)
    {
        //source: chatgpt

        double denom = (double) ((yPoints[1] - yPoints[2]) * (xPoints[0] - xPoints[2]) +
                (xPoints[2] - xPoints[1]) * (yPoints[0] - yPoints[2]));

        //barycentric weights
        double w1 = ((yPoints[1] - yPoints[2]) * (x - xPoints[2]) +
                (xPoints[2] - xPoints[1]) * (y - yPoints[2])) / denom;
        double w2 = ((yPoints[2] - yPoints[0]) * (x - xPoints[2]) +
                (xPoints[0] - xPoints[2]) * (y - yPoints[2])) / denom;
        double w3 = 1.0f - w1 - w2;

        double uInterp = w1 * u[0] + w2 * u[1] + w3 * u[2];
        double vInterp = w1 * v[0] + w2 * v[1] + w3 * v[2];

        return new double[] { uInterp, vInterp };
    }

    private static boolean isInsideTriangle(int x, int y, int[] xPoints, int[] yPoints)
    {
        //source: chatgpt

        int x1 = xPoints[0], y1 = yPoints[0];
        int x2 = xPoints[1], y2 = yPoints[1];
        int x3 = xPoints[2], y3 = yPoints[2];

        // calculate cross product (sign determines on which side of the edge the point is)
        int d1 = (x - x2) * (y1 - y2) - (y - y2) * (x1 - x2);
        int d2 = (x - x3) * (y2 - y3) - (y - y3) * (x2 - x3);
        int d3 = (x - x1) * (y3 - y1) - (y - y1) * (x3 - x1);

        boolean sameSide = (d1 < 0 && d2 < 0 && d3 < 0) || (d1 > 0 && d2 > 0 && d3 > 0);
        return sameSide;
    }

    private static Color getShade(Color color, double shade)
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
