package main.java.gameobjects.shapes;

import java.awt.*;

import main.java.gameobjects.GameObject;
import main.java.meth.Mat4;
import main.java.meth.Vec3;
import main.java.meth.Vec4;
import main.java.rendering.Camera;
import main.java.rendering.Renderer;

import main.java.rendering.*;

public abstract class ColoredGameObject extends GameObject
{
    /**
     * The vertices of the gameobject (will have to be set by the derived classes)
     */
    protected double[] vertices;
    /**
     * The color of the gameobject
     */
    protected Color color;

    /**
     * Creates a colored gameobject with a given position, scale, rotation and color
     */
    public ColoredGameObject(Vec3 position, Vec3 scale, Vec3 rotation, Color color)
    {
        translate(position);
        scale(scale);
        rotate(rotation);

        this.vertices = null;
        this.color = color;
    }

    /**
     * Returns the color of the gameobject
     */
    public Color getColor() { return color; }
    /**
     * Sets the color of the gameobject
     */
    public void setColor(Color color) { this.color = color; }

    public void update(double dt) { }

    /**
     * Default rendering algorithm for gameobjects (draws all triangles seperately)
     * @param camera From what pov the gameobject will be rendered
     */
    public void render(Camera camera)
    {
        Mat4 viewProj = camera.getProjectionMatrix().multiply(camera.getViewMatrix());
        Mat4 model = getModelMatrix();

        for (int i = 0; i < vertices.length; i += 3 * 5) {
            // model->world
            Vec4[] trigon = new Vec4[3];
            for (int j = 0; j < 3; j++) {
                Vec4 vertex1 = new Vec4(
                        vertices[j * 5 + i + 0],
                        vertices[j * 5 + i + 1],
                        vertices[j * 5 + i + 2],
                        1.0
                );
                trigon[j] = model.multiply(vertex1);
            }

            // shading and back-face culling in world space: (quadCenter - camPos) * normal should be positive to be drawn
            Vec3 center = trigon[0].add(trigon[1]).add(trigon[2]).xyz().scale(0.33);
            Vec3 normal = trigon[2].xyz().subtract(trigon[0].xyz()).cross(trigon[1].xyz().subtract(trigon[0].xyz())).normalize();
            double dot = (center.subtract(camera.getPosition())).normalize().dot(normal);
            if (Renderer.isBackfaceCullingEnabled() && dot <= 0.0)
                continue;

            // world->view->clip
            for (int j = 0; j < 3; j++) {
                trigon[j] = viewProj.multiply(trigon[j]);
                trigon[j].setX(trigon[j].getX() / trigon[j].getW());
                trigon[j].setY(trigon[j].getY() / trigon[j].getW());
                trigon[j].setZ(trigon[j].getZ() / trigon[j].getW());
            }

            // clip->screen
            double scaleH = Renderer.getWidth() / 2.0;
            double scaleV = Renderer.getHeight() / 2.0;
            Renderer.drawTriangle(new Vec3[] { trigon[0].xyz(), trigon[1].xyz(), trigon[2].xyz() }, ColoredGameObject.getShade(color, dot));
        }
    }

    /**
     * Performs an approximate conversion on a color from scaled format to linear format
     * @return The resulting color of the conversion
     */
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
