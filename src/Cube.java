import java.awt.*;

public class Cube implements Renderable
{
    private double[] vertices;
    private Mat4 model;

    public Cube()
    {
        this(new Vec3(0.0), new Vec3(1.0), new Vec3(0.0));
    }
    public Cube(Vec3 position, Vec3 scale, Vec3 rotation)
    {
        this.model = new Mat4(1.0);
        this.model = Meth.translate(model, position);
        this.model = Meth.scale(model, scale);
        this.model = Meth.rotateX(model, rotation.getX()); // yaw
        this.model = Meth.rotateY(model, rotation.getY()); // pitch
        this.model = Meth.rotateZ(model, rotation.getZ()); // roll

        this.vertices = new double[] {
                // back face
                 0.5,  0.5, -0.5,
                 0.5, -0.5, -0.5,
                -0.5, -0.5, -0.5,
                -0.5, -0.5, -0.5,
                -0.5,  0.5, -0.5,
                 0.5,  0.5, -0.5,
                // front face
                -0.5, -0.5,  0.5,
                 0.5, -0.5,  0.5,
                 0.5,  0.5,  0.5,
                 0.5,  0.5,  0.5,
                -0.5,  0.5,  0.5,
                -0.5, -0.5,  0.5,
                // left face
                -0.5,  0.5,  0.5,
                -0.5,  0.5, -0.5,
                -0.5, -0.5, -0.5,
                -0.5, -0.5, -0.5,
                -0.5, -0.5,  0.5,
                -0.5,  0.5,  0.5,
                // right face
                 0.5, -0.5, -0.5,
                 0.5,  0.5, -0.5,
                 0.5,  0.5,  0.5,
                 0.5,  0.5,  0.5,
                 0.5, -0.5,  0.5,
                 0.5, -0.5, -0.5,
                // bottom face
                -0.5, -0.5, -0.5,
                 0.5, -0.5, -0.5,
                 0.5, -0.5,  0.5,
                 0.5, -0.5,  0.5,
                -0.5, -0.5,  0.5,
                -0.5, -0.5, -0.5,
                // top face
                 0.5,  0.5,  0.5,
                 0.5,  0.5, -0.5,
                -0.5,  0.5, -0.5,
                -0.5,  0.5, -0.5,
                -0.5,  0.5,  0.5,
                 0.5,  0.5,  0.5
        };
    }

    public void _rotate(double dt) { model = Meth.rotateY(model, 30 * dt); }

    public double[] getVertices() { return vertices; }
    public Mat4 getModel() { return model; }
}
