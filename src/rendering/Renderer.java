package rendering;

import gameobjects.GameObject;
import math.Meth;
import math.Vec3;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.*;
import java.util.List;

public class Renderer
{
    // target canvas for the rendering
    private static Canvas canvas;

    private static boolean backfaceCulling = true;
    private static boolean wireframe = false;

    // strings to render
    private static final Font bigFont = new Font("Monospaced", Font.BOLD, 40);
    private static List<RendererString> rendererStrings;

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
                // bottom face
                -0.5,  0.0, -0.5,  1.0, 0.0,
                 0.5,  0.0, -0.5,  0.0, 0.0,
                 0.5,  0.0,  0.5,  0.0, 1.0,
                 0.5,  0.0,  0.5,  0.0, 1.0,
                -0.5,  0.0,  0.5,  1.0, 1.0,
                -0.5,  0.0, -0.5,  1.0, 0.0,
                // top face
                 0.5,  0.0,  0.5,  1.0, 1.0,
                 0.5,  0.0, -0.5,  1.0, 0.0,
                -0.5,  0.0, -0.5,  0.0, 0.0,
                -0.5,  0.0, -0.5,  0.0, 0.0,
                -0.5,  0.0,  0.5,  0.0, 1.0,
                 0.5,  0.0,  0.5,  1.0, 1.0
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

    public static int getWidth() { return canvas.getWidth(); }
    public static int getHeight() { return canvas.getHeight(); }

    public static void setBackfaceCulling(boolean value) { backfaceCulling = value; }
    public static boolean isBackfaceCullingEnabled() { return backfaceCulling; }

    public static void setWireframe(boolean value) { wireframe = value; }
    public static boolean isWireframeEnabled() { return wireframe; }

    public static void init(Canvas canvas)
    {
        Renderer.canvas = canvas;

        canvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateFramebuffer();
                canvas.repaint();
            }
        });

        rendererStrings = new ArrayList<>();

        updateFramebuffer();
    }

    public static void render(Graphics2D g2d, Camera camera, List<GameObject> objects)
    {
        fbG2d = framebuffer.createGraphics();

        // clear background
        Arrays.fill(fbPixels, 0xFF000000);

        // sort objects based on the priority first and then on the distance from camera (render least priority nearest last)
        Collections.sort(objects,
                Comparator.comparing(GameObject::getRenderingPriority).thenComparing(
                Comparator.comparingDouble(
                        (GameObject o) -> (o.getPosition().subtract(camera.getPosition())).getLengthSquared()).reversed()
                )
        );

        // draw objects to the framebuffer
        fbG2d.translate(canvas.getWidth() / 2 * resolution, canvas.getHeight() / 2 * resolution);
        fbG2d.scale(1.0, -1.0);
        for (GameObject obj : objects)
            obj.render(camera);

        // draw the framebuffer
        fbG2d.dispose();
        g2d.drawImage(framebuffer, 0, 0, canvas.getWidth(), canvas.getHeight(), null);

        int i = 1;
        g2d.setFont(bigFont);
        for (RendererString string : rendererStrings)
        {
            g2d.setColor(string.color);
            g2d.drawString(string.string, 10, i++ * 50);
        }
        rendererStrings.clear();
    }

    public static void drawTriangle(Vec3[] trigon, Color color)
    {
        if (!Meth.isBetween(trigon[0].getZ(), -1.0, 1.0) ||
            !Meth.isBetween(trigon[1].getZ(), -1.0, 1.0) ||
            !Meth.isBetween(trigon[2].getZ(), -1.0, 1.0))
            return;

        _drawTriangleImpl(trigon, color);
    }

    public static void drawQuad(Vec3[] trigon1, Vec3[] trigon2, Color color)
    {
        if (!Meth.isBetween(trigon1[0].getZ(), -1.0, 1.0) ||
            !Meth.isBetween(trigon1[1].getZ(), -1.0, 1.0) ||
            !Meth.isBetween(trigon1[2].getZ(), -1.0, 1.0))
            return;

        if (!Meth.isBetween(trigon2[0].getZ(), -1.0, 1.0) ||
            !Meth.isBetween(trigon2[1].getZ(), -1.0, 1.0) ||
            !Meth.isBetween(trigon2[2].getZ(), -1.0, 1.0))
            return;

        _drawTriangleImpl(trigon1, color);
        _drawTriangleImpl(trigon2, color);
    }

    public static void drawTexturedTriangle(BufferedImage texture, Vec3[] trigon, double[] u, double[] v)
    {
        // discard clipped vertices (triangles)
        if (!Meth.isBetween(trigon[0].getZ(), -1.0, 1.0) ||
            !Meth.isBetween(trigon[1].getZ(), -1.0, 1.0) ||
            !Meth.isBetween(trigon[2].getZ(), -1.0, 1.0))
            return;

        _drawTexturedTriangleImpl(texture, trigon, u, v);
    }

    public static void addString(RendererString string) { rendererStrings.add(string); }

    public static void _drawTriangleImpl(Vec3[] trigon, Color color)
    {
        // clip->screen
        double scaleH = getWidth() / 2.0;
        double scaleV = getHeight() / 2.0;
        int[] xCoords = new int[] { (int) (trigon[0].getX() * scaleH * resolution), (int) (trigon[1].getX() * scaleH * resolution), (int) (trigon[2].getX() * scaleH * resolution) };
        int[] yCoords = new int[] { (int) (trigon[0].getY() * scaleV * resolution), (int) (trigon[1].getY() * scaleV * resolution), (int) (trigon[2].getY() * scaleV * resolution) };

        fbG2d.setColor(color);
        if (wireframe)
            fbG2d.drawPolygon(xCoords, yCoords, 3);
        else
            fbG2d.fillPolygon(xCoords, yCoords, 3);
    }

    public static void _drawTexturedTriangleImpl(BufferedImage texture, Vec3[] trigon, double[] u, double[] v)
    {
        double scaleH = Renderer.getWidth() / 2.0;
        double scaleV = Renderer.getHeight() / 2.0;
        int[] xCoords = new int[] { (int) (trigon[0].getX() * scaleH), (int) (trigon[1].getX() * scaleH), (int) (trigon[2].getX() * scaleH) };
        int[] yCoords = new int[] { (int) (trigon[0].getY() * scaleV), (int) (trigon[1].getY() * scaleV), (int) (trigon[2].getY() * scaleV) };

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

                    int fbX = (int) ((x + (double) canvas.getWidth() / 2) * resolution);
                    int fbY = (int) ((-y + (double) canvas.getHeight() / 2) * resolution);
                    if (fbX < 0 || framebuffer.getWidth() <= fbX) break;
                    if (0 <= fbY && fbY < framebuffer.getHeight())
                    {
                        int color = texture.getRGB(texX, texY);
                        // if the pixel is not transparent
                        if (((color >> 24) & 0xFF) != 0)
                            fbPixels[fbY * framebuffer.getWidth() + fbX] = color;
                    }

                    if (wireframe) y = y2 + 0.000001;
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

                    int fbX = (int) ((x + (double) canvas.getWidth() / 2) * resolution);
                    int fbY = (int) ((-y + (double) canvas.getHeight() / 2) * resolution);
                    if (fbX < 0 || framebuffer.getWidth() <= fbX) break;
                    if (0 <= fbY && fbY < framebuffer.getHeight())
                    {
                        int color = texture.getRGB(texX, texY);
                        // if the pixel is not transparent
                        if (((color >> 24) & 0xFF) != 0)
                            fbPixels[fbY * framebuffer.getWidth() + fbX] = color;
                    }

                    if (wireframe) y = y2 + 0.000001;
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
        // if the Rendering.rendering.Renderer has not yet been initialized
        if (canvas.getWidth() <= 0 || canvas.getHeight() <= 0) return;

        framebuffer = new BufferedImage((int)(canvas.getWidth() * resolution), (int)(canvas.getHeight() * resolution), BufferedImage.TYPE_INT_ARGB);
        fbPixels = ((DataBufferInt) framebuffer.getRaster().getDataBuffer()).getData();
    }
}
