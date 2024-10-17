public class Meth
{
    final static double DEG2RAD = Math.PI / 180.0;

    public static Mat4 scale(Vec3 s)
    {
        return new Mat4(new double[] {
                s.getX(), 0.0,      0.0,      0.0,
                0.0,      s.getY(), 0.0,      0.0,
                0.0,      0.0,      s.getZ(), 0.0,
                0.0,      0.0,      0.0,      1.0
        });
    }

    public static Mat4 rotateX(double angle)
    {
        angle *= DEG2RAD;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        return new Mat4(new double[] {
                1.0, 0.0,  0.0, 0.0,
                0.0, cos, -sin, 0.0,
                0.0, sin,  cos, 0.0,
                0.0, 0.0,  0.0, 1.0
        });
    }
    public static Mat4 rotateY(double angle)
    {
        angle *= DEG2RAD;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        return new Mat4(new double[] {
                 cos, 0.0, sin, 0.0,
                 0.0, 1.0, 0.0, 0.0,
                -sin, 0.0, cos, 0.0,
                 0.0, 0.0, 0.0, 1.0
        });
    }
    public static Mat4 rotateZ(double angle)
    {
        angle *= DEG2RAD;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        return new Mat4(new double[] {
                cos, -sin, 0.0, 0.0,
                sin,  cos, 0.0, 0.0,
                0.0,  0.0, 1.0, 0.0,
                0.0,  0.0, 0.0, 1.0
        });
    }

    public static Mat4 rotate(Vec3 axis, double angle)
    {
        axis = axis.normalize();
        angle *= DEG2RAD;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double omcos = 1.0 - cos;

        return new Mat4(new double[] {
                cos + Math.pow(axis.getX(), 2) * omcos, axis.getX() * axis.getY() * omcos - axis.getZ() * sin, axis.getX() * axis.getZ() * omcos + axis.getY() * sin, 0.0,
                axis.getY() * axis.getX() * omcos + axis.getZ() * sin, cos + Math.pow(axis.getY(), 2) * omcos, axis.getY() * axis.getZ() * omcos - axis.getX() * sin, 0.0,
                axis.getZ() * axis.getX() * omcos - axis.getY() * sin, axis.getZ() * axis.getY() * omcos + axis.getX() * sin, cos + Math.pow(axis.getZ(), 2) * omcos, 0.0,
                0.0, 0.0, 0.0, 1.0
        });
    }

    public static Mat4 translate(Vec3 t)
    {
        return new Mat4(new double[] {
                1.0, 0.0, 0.0, t.getX(),
                0.0, 1.0, 0.0, t.getY(),
                0.0, 0.0, 1.0, t.getZ(),
                0.0, 0.0, 0.0, 1.0
        });
    }

    public static Mat4 perspective(double fovY, double aspect, double near, double far) //TODO: fix this
    {
        double cot = 1.0 / Math.tan(fovY / 2 * DEG2RAD);
        return new Mat4(new double[] {
                cot / aspect, 0.0,  0.0,                              0.0,
                0.0,          cot,  0.0,                              0.0,
                0.0,          0.0, -(far + near) / (far - near),     -1.0,
                0.0,          0.0, -(2 * far * near) / (far - near),  0.0
        });
    }
    public static Mat4 orthographic(double left, double right, double top, double bottom, double near, double far)
    {
        return new Mat4(new double[] {
                2.0 / (right - left), 0.0,                   0.0,                -(right + left) / (right - left),
                0.0,                  2.0 / (top - bottom),  0.0,                -(top + bottom) / (top - bottom),
                0.0,                  0.0,                  -2.0 / (far - near), -(far + near) / (far - near),
                0.0,                  0.0,                   0.0,                 1.0
        });
    }
}
