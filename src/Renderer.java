import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.*;
import java.util.List;

public class Renderer
{
    // target canvas for the rendering
    private static Canvas canvas;
    private static int width = 0, height = 0;

    private static final List<Renderable> objects = new ArrayList<>();

    // framebuffer for the textures
    private static double resolution = 1.0;
    private static BufferedImage framebuffer;
    private static Graphics2D fbG2d;
    private static int[] fbPixels;

    // data for the objects
    public static double[] PlainVertices;
    public static double[] CubeVertices;
    public static double[] TetrahedronVertices;

    static {
        PlainVertices = new double[]{
                // front face
                -0.5, -0.5, 0.0, 0.0, 1.0,
                 0.5, -0.5, 0.0, 1.0, 1.0,
                 0.5,  0.5, 0.0, 1.0, 0.0,
                 0.5,  0.5, 0.0, 1.0, 0.0,
                -0.5,  0.5, 0.0, 0.0, 0.0,
                -0.5, -0.5, 0.0, 0.0, 1.0,
                // back face
                 0.5,  0.5, 0.0, 1.0, 0.0,
                 0.5, -0.5, 0.0, 1.0, 1.0,
                -0.5, -0.5, 0.0, 0.0, 1.0,
                -0.5, -0.5, 0.0, 0.0, 1.0,
                -0.5,  0.5, 0.0, 0.0, 0.0,
                 0.5,  0.5, 0.0, 1.0, 0.0
        };
        CubeVertices = new double[] {
                // back face
                 0.5,  0.5, -0.5,  0.0, 0.0,
                 0.5, -0.5, -0.5,  0.0, 1.0,
                -0.5, -0.5, -0.5,  1.0, 1.0,
                -0.5, -0.5, -0.5,  1.0, 1.0,
                -0.5,  0.5, -0.5,  1.0, 0.0,
                 0.5,  0.5, -0.5,  0.0, 0.0,
                // front face
                -0.5, -0.5,  0.5,  0.0, 1.0,
                 0.5, -0.5,  0.5,  1.0, 1.0,
                 0.5,  0.5,  0.5,  1.0, 0.0,
                 0.5,  0.5,  0.5,  1.0, 0.0,
                -0.5,  0.5,  0.5,  0.0, 0.0,
                -0.5, -0.5,  0.5,  0.0, 1.0,
                // left face
                -0.5,  0.5,  0.5,  1.0, 0.0,
                -0.5,  0.5, -0.5,  0.0, 0.0,
                -0.5, -0.5, -0.5,  0.0, 1.0,
                -0.5, -0.5, -0.5,  0.0, 1.0,
                -0.5, -0.5,  0.5,  1.0, 1.0,
                -0.5,  0.5,  0.5,  1.0, 0.0,
                // right face
                 0.5, -0.5, -0.5,  1.0, 1.0,
                 0.5,  0.5, -0.5,  1.0, 0.0,
                 0.5,  0.5,  0.5,  0.0, 0.0,
                 0.5,  0.5,  0.5,  0.0, 0.0,
                 0.5, -0.5,  0.5,  0.0, 1.0,
                 0.5, -0.5, -0.5,  1.0, 1.0,
                // bottom face
                -0.5, -0.5, -0.5,  0.0, 1.0,
                 0.5, -0.5, -0.5,  1.0, 1.0,
                 0.5, -0.5,  0.5,  1.0, 0.0,
                 0.5, -0.5,  0.5,  1.0, 0.0,
                -0.5, -0.5,  0.5,  0.0, 0.0,
                -0.5, -0.5, -0.5,  0.0, 1.0,
                // top face
                 0.5,  0.5,  0.5,  1.0, 1.0,
                 0.5,  0.5, -0.5,  1.0, 0.0,
                -0.5,  0.5, -0.5,  0.0, 0.0,
                -0.5,  0.5, -0.5,  0.0, 0.0,
                -0.5,  0.5,  0.5,  0.0, 1.0,
                 0.5,  0.5,  0.5,  1.0, 1.0
        };
        TetrahedronVertices = new double[] {
                // face 1
                -0.5,  0.5, -0.5,  0.5, 0.0,
                -0.5, -0.5,  0.5,  0.0, 1.0,
                 0.5,  0.5,  0.5,  1.0, 1.0,
                // face 2
                 0.5,  0.5,  0.5,  0.5, 0.0,
                -0.5, -0.5,  0.5,  0.0, 1.0,
                 0.5, -0.5, -0.5,  1.0, 1.0,
                // face 3
                 0.5,  0.5,  0.5,  0.5, 0.0,
                 0.5, -0.5, -0.5,  0.0, 1.0,
                -0.5,  0.5, -0.5,  1.0, 1.0,
                // face 4
                -0.5,  0.5, -0.5,  0.5, 0.0,
                 0.5, -0.5, -0.5,  0.0, 1.0,
                -0.5, -0.5,  0.5,  1.0, 1.0
        };
    }

    // res has to be between 0 and 1
    public static void setResolution(double res)
    {
        resolution = Math.clamp(res, 0.01, 1.0);
        updateFramebuffer();
    }
    public static double getResolution() { return resolution; }

    public static int getWidth() { return width; }
    public static int getHeight() { return height; }

    public static void init(Canvas canvas)
    {
        Renderer.canvas = canvas;

        canvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                width = canvas.getWidth();
                height = canvas.getHeight();

                updateFramebuffer();
                canvas.repaint();
            }
        });

        Renderer.width = canvas.getWidth();
        Renderer.height = canvas.getHeight();

        AssetManager.loadTexture("bg", "res/ksi.png");

        updateFramebuffer();
    }

    public static void addRenderable(Renderable obj) { objects.add(obj); }
    public static void removeRenderable(Renderable obj) { objects.remove(obj); }
    public static void clearRenderables() { objects.clear(); }

    public static void render(Graphics2D g2d, Camera camera)
    {
        fbG2d = framebuffer.createGraphics();

        // clear background
        Arrays.fill(fbPixels, 0xFF000000);

        // draw KSI bg
        AffineTransform transform = new AffineTransform();
        transform.shear(Math.cos(System.currentTimeMillis() / 179.3), Math.sin(System.currentTimeMillis() / 1311.0));
        transform.scale(width / (double) AssetManager.getTexture("bg").get().getWidth() * resolution, (double)height / AssetManager.getTexture("bg").get().getHeight() * resolution);
        transform.translate(100 -(int)(Math.pow(Math.sin(System.currentTimeMillis() / 1000.0),2.0) * 300), 0);
        fbG2d.drawImage(AssetManager.getTexture("bg").get(), transform, null);

        // sort objects based on distance from camera (render nearest last)
        Collections.sort(objects, Comparator.comparingDouble((Renderable o) -> (o.getPosition().subtract(camera.getPosition())).getLengthSquared()).reversed());

        // draw objects to the framebuffer
        fbG2d.translate(width / 2 * resolution, height / 2 * resolution);
        fbG2d.scale(1.0, -1.0);
        for (Renderable obj : objects)
            obj.render(camera);

        // draw the framebuffer
        fbG2d.dispose();
        g2d.drawImage(framebuffer, 0, 0, width, height, null);
    }

    public static void drawTriangle(int[] xCoords, int[] yCoords, Color color)
    {
        fbG2d.setColor(color);
        for (int i = 0; i < 3; i++)
        {
            xCoords[i] = (int) (xCoords[i] * resolution);
            yCoords[i] = (int) (yCoords[i] * resolution);
        }
        fbG2d.fillPolygon(xCoords, yCoords, 3);
    }

    public static void drawTexturedTriangle(BufferedImage texture, int[] xCoords, int[] yCoords, double[] u, double[] v)
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

        // if there is a 'left triangle' to be drawn
        if (xCoords[v0] != xCoords[v1])
        {
            // slopes for the sides
            double m1 = (double) (yCoords[v1] - yCoords[v0]) / (xCoords[v1] - xCoords[v0]);
            double m2 = (double) (yCoords[v2] - yCoords[v0]) / (xCoords[v2] - xCoords[v0]);

            // m1 has to be bigger
            if (m2 > m1)
            {
                double tmp = m1;
                m1 = m2;
                m2 = tmp;
            }

            // iterate through the columns from the left until the middle
            double y1 = yCoords[v0], y2 = yCoords[v0];
            for (int x = xCoords[v0]; x < xCoords[v1]; x++)
            {
                y1 += m1;
                y2 += m2;

                // iterate through the pixels in the column
                for (double y = y1; y >= y2; y--)
                {
                    double[] uv = interpolateUV(x, (int) y, xCoords, yCoords, u, v);
                    int texX = Math.clamp((int) (uv[0] * texture.getWidth()), 0, texture.getWidth() - 1);
                    int texY = Math.clamp((int) (uv[1] * texture.getHeight()), 0, texture.getHeight() - 1);

                    int fbX = (int) ((x + (double) width / 2) * resolution);
                    int fbY = (int) ((-y + (double) height / 2) * resolution);
                    if (fbX < 0 || framebuffer.getWidth() <= fbX) break;
                    if (0 <= fbY && fbY < framebuffer.getHeight())
                    {
                        int color = texture.getRGB(texX, texY);
                        // if the pixel is not transparent
                        if (((color >> 24) & 0xFF) != 0)
                            fbPixels[fbY * framebuffer.getWidth() + fbX] = color;
                    }
                }
            }
        }

        // if there is a 'right triangle' to be drawn
        if (xCoords[v1] != xCoords[v2])
        {
            // slopes for the sides
            double m1 = (double) (yCoords[v1] - yCoords[v2]) / (xCoords[v1] - xCoords[v2]);
            double m2 = (double) (yCoords[v0] - yCoords[v2]) / (xCoords[v0] - xCoords[v2]);

            // m1 has to be smaller
            if (m2 < m1)
            {
                double tmp = m1;
                m1 = m2;
                m2 = tmp;
            }

            // iterate through the columns from the right until the middle
            double y1 = yCoords[v2], y2 = yCoords[v2];
            for (int x = xCoords[v2]; x > xCoords[v1]; x--)
            {
                y1 -= m1;
                y2 -= m2;

                // iterate through the pixels in the column
                for (double y = y1; y >= y2; y--)
                {
                    double[] uv = interpolateUV(x, (int) y, xCoords, yCoords, u, v);
                    int texX = Math.clamp((int) (uv[0] * texture.getWidth()), 0, texture.getWidth() - 1);
                    int texY = Math.clamp((int) (uv[1] * texture.getHeight()), 0, texture.getHeight() - 1);

                    int fbX = (int) ((x + (double) width / 2) * resolution);
                    int fbY = (int) ((-y + (double) height / 2) * resolution);
                    if (fbX < 0 || framebuffer.getWidth() <= fbX) break;
                    if (0 <= fbY && fbY < framebuffer.getHeight())
                    {
                        int color = texture.getRGB(texX, texY);
                        // if the pixel is not transparent
                        if (((color >> 24) & 0xFF) != 0)
                            fbPixels[fbY * framebuffer.getWidth() + fbX] = color;
                    }
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

    private static void updateFramebuffer()
    {
        // if the Renderer has not yet been initialized
        if (width <= 0 || height <= 0) return;

        framebuffer = new BufferedImage((int)(width * resolution), (int)(height * resolution), BufferedImage.TYPE_INT_ARGB);
        fbPixels = ((DataBufferInt) framebuffer.getRaster().getDataBuffer()).getData();
    }

//    private static boolean isInsideTriangle(int x, int y, int[] xPoints, int[] yPoints)
//    {
//        //source: chatgpt
//
//        int x1 = xPoints[0], y1 = yPoints[0];
//        int x2 = xPoints[1], y2 = yPoints[1];
//        int x3 = xPoints[2], y3 = yPoints[2];
//
//        // calculate cross product (sign determines on which side of the edge the point is)
//        int d1 = (x - x2) * (y1 - y2) - (y - y2) * (x1 - x2);
//        int d2 = (x - x3) * (y2 - y3) - (y - y3) * (x2 - x3);
//        int d3 = (x - x1) * (y3 - y1) - (y - y1) * (x3 - x1);
//
//        return (d1 < 0 && d2 < 0 && d3 < 0) || (d1 > 0 && d2 > 0 && d3 > 0);
//    }
}
