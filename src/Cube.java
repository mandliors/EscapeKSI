import java.awt.*;

public class Cube implements Renderable
{
    private Trigon[] trigons;
    private Mat4 model;

    public Cube()
    {
        this.model = new Mat4(1.0);
        this.model = Meth.translate(model, new Vec3(-0.5, 0, 10.2));
        this.model = Meth.scale(model, new Vec3(0.5));
        this.model = Meth.rotate(model, new Vec3(1, 0.2, 0.5), 45.0);

        trigons = new Trigon[]{
                //back face
                new Trigon(
                        new Vec3( 0.5,  0.5, -0.5),
                        new Vec3( 0.5, -0.5, -0.5),
                        new Vec3(-0.5, -0.5, -0.5)
                ),
                new Trigon(
                        new Vec3(-0.5, -0.5, -0.5),
                        new Vec3(-0.5,  0.5, -0.5),
                        new Vec3( 0.5,  0.5, -0.5)
                ),
                //front face
                new Trigon(
                        new Vec3(-0.5, -0.5,  0.5),
                        new Vec3( 0.5, -0.5,  0.5),
                        new Vec3( 0.5,  0.5,  0.5)
                ),
                new Trigon(
                        new Vec3( 0.5,  0.5,  0.5),
                        new Vec3(-0.5,  0.5,  0.5),
                        new Vec3(-0.5, -0.5,  0.5)
                ),
                // left face
                new Trigon(
                        new Vec3(-0.5,  0.5,  0.5),
                        new Vec3(-0.5,  0.5, -0.5),
                        new Vec3(-0.5, -0.5, -0.5)
                ),
                new Trigon(
                        new Vec3(-0.5, -0.5, -0.5),
                        new Vec3(-0.5, -0.5,  0.5),
                        new Vec3(-0.5,  0.5,  0.5)
                ),
                // right face
                new Trigon(
                        new Vec3(0.5, -0.5, -0.5),
                        new Vec3(0.5,  0.5, -0.5),
                        new Vec3(0.5,  0.5,  0.5)
                ),
                new Trigon(
                        new Vec3(0.5,  0.5,  0.5),
                        new Vec3(0.5, -0.5,  0.5),
                        new Vec3(0.5, -0.5, -0.5)
                ),
                // bottom face
                new Trigon(
                        new Vec3(-0.5, -0.5, -0.5),
                        new Vec3( 0.5, -0.5, -0.5),
                        new Vec3( 0.5, -0.5,  0.5)
                ),
                new Trigon(
                        new Vec3( 0.5, -0.5,  0.5),
                        new Vec3(-0.5, -0.5,  0.5),
                        new Vec3(-0.5, -0.5, -0.5)
                ),
                // top face
                new Trigon(
                        new Vec3( 0.5,  0.5,  0.5),
                        new Vec3( 0.5,  0.5, -0.5),
                        new Vec3(-0.5,  0.5, -0.5)
                ),
                new Trigon(
                        new Vec3(-0.5,  0.5, -0.5),
                        new Vec3(-0.5,  0.5,  0.5),
                        new Vec3( 0.5,  0.5,  0.5)
                )
        };
    }

    public void _rotate() { model = Meth.rotateY(model, 0.5); }

    public Trigon[] getTrigons() { return trigons; }
    public Mat4 getModel() { return model; }
}