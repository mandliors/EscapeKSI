public class Meth
{
    final static double DEG2RAD = Math.PI / 180.0;

    public static Mat4 scale(Mat4 mat, Vec3 s)
    {
        return new Mat4(new double[] {
                s.getX(), 0.0,      0.0,      0.0,
                0.0,      s.getY(), 0.0,      0.0,
                0.0,      0.0,      s.getZ(), 0.0,
                0.0,      0.0,      0.0,      1.0
        });
    }

    public static Mat4 rotateX(Mat4 mat, double angle)
    {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        return new Mat4(new double[] {
                1.0,  0.0, 0.0, 0.0,
                0.0,  cos, sin, 0.0,
                0.0, -sin, cos, 0.0,
                0.0,  0.0, 0.0, 1.0
        });
    }
    public static Mat4 rotateY(Mat4 mat, double angle)
    {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        return new Mat4(new double[] {
                cos, 0.0, -sin, 0.0,
                0.0, 1.0,  0.0, 0.0,
                sin, 0.0,  cos, 0.0,
                0.0, 0.0,  0.0, 1.0
        });
    }
    public static Mat4 rotateZ(Mat4 mat, double angle)
    {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        return new Mat4(new double[] {
                cos, -sin, 0.0, 0.0,
                sin,  cos, 0.0, 0.0,
                0.0,  0.0, 1.0, 0.0,
                0.0,  0.0, 0.0, 1.0
        });
    }

    public static Mat4 translate(Mat4 mat, Vec3 t)
    {
        return new Mat4(new double[] {
                1.0,      0.0,      0.0,      0.0,
                0.0,      1.0,      0.0,      0.0,
                0.0,      0.0,      1.0,      0.0,
                t.getX(), t.getY(), t.getZ(), 1.0
        });
    }

    public static Mat4 perspective(double fovX, double aspect, double near, double far)
    {
        double cotangent = 1.0 / Math.tan(fovX / 2 * DEG2RAD);

        return new Mat4(new double[] {
                cotangent / aspect, 0.0,       0.0,                              0.0,
                0.0,                cotangent, 0.0,                              0.0,
                0.0,                0.0,       -(far + near) / (far - near),    -1.0,
                0.0,                0.0,       -(2 * far * near) / (far - near), 0.0
        });
    }
    public static Mat4 orthographic(double left, double right, double top, double bottom, double near, double far)
    {
        return new Mat4(new double[] {
                2.0 / (right - left),              0.0,                              0.0,                         0.0,
                0.0,                               2.0 / (top - bottom),             0.0,                         0.0,
                0.0,                               0.0,                             -2.0 / (far - near),          0.0,
                -(right + left) / (right - left), -(top + bottom) / (top - bottom), -(far + near) / (far - near), 1.0
        });
    }
}
