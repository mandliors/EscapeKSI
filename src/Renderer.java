import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Renderer
{
    // target canvas for the rendering
    private static Canvas canvas;
    private static Graphics2D g2d;
    private static int width, height;

    private static final List<Renderable> objects = new ArrayList<>();
    private static BufferedImage ksi = null;

    // framebuffer for the textures
    private static final double FRAMEBUFFER_SCALE = 1.0;
    private static BufferedImage framebuffer;

    public static void init(Canvas canvas)
    {
        try { ksi = ImageIO.read(new File("res/ksi.png")); }
        catch (IOException e) { ksi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); }

        Renderer.canvas = canvas;

        Renderer.width = canvas.getWidth();
        Renderer.height = canvas.getHeight();

        Renderer.framebuffer = new BufferedImage((int)(width * FRAMEBUFFER_SCALE), (int)(height * FRAMEBUFFER_SCALE), BufferedImage.TYPE_INT_ARGB);
    }

    public static void addRenderable(Renderable obj)
    {
        objects.add(obj);
    }
    public static void removeRenderable(Renderable obj) { objects.remove(obj); }
    public static void clearRenderables() { objects.clear(); }

    public static void render(Camera camera)
    {
        BufferStrategy bufferStrategy = canvas.getBufferStrategy();
        g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, width, height);

        AffineTransform transform = new AffineTransform();
        transform.shear(Math.cos(System.currentTimeMillis() / 179.3), Math.sin(System.currentTimeMillis() / 1311.0));
        transform.scale(width / (double)ksi.getWidth(), (double)height / ksi.getHeight());
        transform.translate(100 -(int)(Math.pow(Math.sin(System.currentTimeMillis() / 1000.0),2.0) * 300), 0);
        g2d.drawImage(ksi, transform, null);


        g2d.translate(width / 2, height / 2);
        g2d.scale(1.0, -1.0);

        // sort objects based on distance from camera (render nearest last)
        Collections.sort(objects, Comparator.comparingDouble((Renderable o) -> (o.getPosition().subtract(camera.getPosition())).getLengthSquared()).reversed());

        for (Renderable obj : objects)
        {
            obj.render(camera);

            // shading
//            g2d.setColor(getShade(Color.CYAN, dot));
//            if (i == 45)
//            {
//                double[] u = new double[] { 0.0, 1.0, 1.0 };
//                double[] v = new double[] { 1.0, 1.0, 0.0 };
//                drawTexturedTriangleToFramebuffer(ksi, xCoords, yCoords, u, v);
//            }
//            else if (i == 36)
//            {
//                double[] u = new double[] { 1.0, 0.0, 0.0 };
//                double[] v = new double[] { 0.0, 0.0, 1.0 };
//                drawTexturedTriangleToFramebuffer(ksi, xCoords, yCoords, u, v);
//            }

            // draw normal
//                g2d.setColor(Color.YELLOW);
//                g2d.fillOval((int) (start.getX() * width / 2.0) - 5, (int) (start.getY() * height / 2.0) - 5, 10, 10);
//                g2d.drawLine((int) (start.getX() * width / 2.0), (int) (start.getY() * height / 2.0), (int) (end.getX() * width / 2.0), (int) (end.getY() * height / 2.0));
        }
        g2d.translate(-width / 2, -height / 2);
        g2d.drawImage(framebuffer, 0, 0, width, height, null);

        // clear the framebuffer
        int[] pixels = ((DataBufferInt) framebuffer.getRaster().getDataBuffer()).getData();
        Arrays.fill(pixels, 0);

        // clean up
        g2d.dispose();
        bufferStrategy.show();
    }

    public static void drawTriangle(int[] xCoords, int[] yCoords, Color color)
    {
        g2d.setColor(color);
        g2d.fillPolygon(xCoords, yCoords, 3);
    }

    private static void drawTexturedTriangleToFramebuffer(BufferedImage texture, int[] xCoords, int[] yCoords, double[] u, double[] v)
    {
        // sort vertices (v0 - left, v1 - middle, v2 - right), store indices for the vertices
        int v0, v1, v2;
        if (xCoords[0] < xCoords[1])
        {
            if (xCoords[0] < xCoords[2])
            {
                v0 = 0;
                if (xCoords[1] < xCoords[2]) { v1 = 1; v2 = 2; }
                else { v1 = 2; v2 = 1; }
            }
            else
            {
                v0 = 2;
                v1 = 0;
                v2 = 1;
            }
        }
        else if (xCoords[2] < xCoords[0])
        {
            v2 = 0;
            if (xCoords[1] < xCoords[2]) { v0 = 1; v1 = 2; }
            else { v0 = 2; v1 = 1; }
        }
        else
        {
            v0 = 1;
            v1 = 0;
            v2 = 2;
        }

        // slopes for the sides
        double m1 = xCoords[v1] == xCoords[v0] ? 0 :  (double)(yCoords[v1] - yCoords[v0]) / (xCoords[v1] - xCoords[v0]);
        double m2 = xCoords[v2] == xCoords[v0] ? 0 : (double)(yCoords[v2] - yCoords[v0]) / (xCoords[v2] - xCoords[v0]);

        // m1 has to be bigger
        if (m2 > m1)
        {
            double tmp = m1;
            m1 = m2;
            m2 = tmp;
        }

        // iterate through the columns from the left until the middle
        double y1 = yCoords[v0], y2 = yCoords[v0];
        for (int x = xCoords[v0]; x <= xCoords[v1]; x++)
        {
            y1 += m1;
            y2 += m2;

            // iterate through the pixels in the column
            for (double y = y1; y >= y2; y--)
            {
                double[] uv = interpolateUV(x, (int)y, xCoords, yCoords, u, v);

                int texX = (int) (uv[0] * texture.getWidth());
                int texY = (int) (uv[1] * texture.getHeight());
                texX = Math.clamp(texX, 0, texture.getWidth() - 1);
                texY = Math.clamp(texY, 0, texture.getHeight() - 1);

                int fbX = (int)((x + (double)width / 2) * FRAMEBUFFER_SCALE);
                int fbY = (int)((y + (double)height / 2) * FRAMEBUFFER_SCALE);
                if (0 <= fbX && fbX < framebuffer.getWidth() &&
                        0 <= fbY && fbY < framebuffer.getHeight())
                {
                    framebuffer.setRGB(fbX, fbY, texture.getRGB(texX, texY));
                }
            }
        }

        // slopes for the sides
        m1 = xCoords[v1] == xCoords[v2] ? 0 : (double)(yCoords[v1] - yCoords[v2]) / (xCoords[v1] - xCoords[v2]);
        m2 = xCoords[v0] == xCoords[v2] ? 0 : (double)(yCoords[v0] - yCoords[v2]) / (xCoords[v0] - xCoords[v2]);

        // m1 has to be smaller
        if (m2 < m1)
        {
            double tmp = m1;
            m1 = m2;
            m2 = tmp;
        }

        // iterate through the columns from the right until the middle
        y1 = yCoords[v2]; y2 = yCoords[v2];
        for (int x = xCoords[v2]; x > xCoords[v1]; x--)
        {
            y1 -= m1;
            y2 -= m2;

            // iterate through the pixels in the column
            for (double y = y1; y >= y2; y--)
            {
                double[] uv = interpolateUV(x, (int)y, xCoords, yCoords, u, v);

                int texX = (int) (uv[0] * texture.getWidth());
                int texY = (int) (uv[1] * texture.getHeight());
                texX = Math.clamp(texX, 0, texture.getWidth() - 1);
                texY = Math.clamp(texY, 0, texture.getHeight() - 1);

                int fbX = (int)((x + (double)width / 2) * FRAMEBUFFER_SCALE);
                int fbY = (int)((y + (double)height / 2) * FRAMEBUFFER_SCALE);
                if (0 <= fbX && fbX < framebuffer.getWidth() &&
                        0 <= fbY && fbY < framebuffer.getHeight())
                {
                    framebuffer.setRGB(fbX, fbY, texture.getRGB(texX, texY));
                }
            }
        }
    }

    public static int getWidth() { return width; }
    public static int getHeight() { return height; }

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
