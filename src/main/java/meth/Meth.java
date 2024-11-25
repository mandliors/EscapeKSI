package main.java.meth;

public class Meth
{
    /**
     * Returns the scaled version of the given matrix
     * @param mat The source matrix
     * @param s The scalar
     * @return The resulting matrix
     */
    public static Mat4 scale(Mat4 mat, Vec3 s)
    {
        return mat.multiply(new Mat4(new double[] {
                s.getX(), 0.0,      0.0,      0.0,
                0.0,      s.getY(), 0.0,      0.0,
                0.0,      0.0,      s.getZ(), 0.0,
                0.0,      0.0,      0.0,      1.0
        }));
    }

    /**
     * Returns the rotated version of the given matrix along the X axis
     * @param mat The source matrix
     * @param angle The rotation angle
     * @return The resulting matrix
     */
    public static Mat4 rotateX(Mat4 mat, double angle)
    {
        angle = Math.toRadians(angle);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        return mat.multiply(new Mat4(new double[] {
                1.0, 0.0,  0.0, 0.0,
                0.0, cos, -sin, 0.0,
                0.0, sin,  cos, 0.0,
                0.0, 0.0,  0.0, 1.0
        }));
    }
    /**
     * Returns the rotated version of the given matrix along the Y axis
     * @param mat The source matrix
     * @param angle The rotation angle
     * @return The resulting matrix
     */
    public static Mat4 rotateY(Mat4 mat, double angle)
    {
        angle = Math.toRadians(angle);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        return mat.multiply(new Mat4(new double[] {
                 cos, 0.0, sin, 0.0,
                 0.0, 1.0, 0.0, 0.0,
                -sin, 0.0, cos, 0.0,
                 0.0, 0.0, 0.0, 1.0
        }));
    }
    /**
     * Returns the rotated version of the given matrix along the Z axis
     * @param mat The source matrix
     * @param angle The rotation angle
     * @return The resulting matrix
     */
    public static Mat4 rotateZ(Mat4 mat, double angle)
    {
        angle = Math.toRadians(angle);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        return mat.multiply(new Mat4(new double[] {
                cos, -sin, 0.0, 0.0,
                sin,  cos, 0.0, 0.0,
                0.0,  0.0, 1.0, 0.0,
                0.0,  0.0, 0.0, 1.0
        }));
    }

    /**
     * Returns the rotated version of the given matrix along an arbitrary axis
     * @param mat The source matrix
     * @param axis The rotation axis
     * @param angle The rotation angle
     * @return The resulting matrix
     */
    public static Mat4 rotate(Mat4 mat, Vec3 axis, double angle)
    {
        axis = axis.normalize();
        angle = Math.toRadians(angle);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double omcos = 1.0 - cos;

        return mat.multiply(new Mat4(new double[] {
                cos + Math.pow(axis.getX(), 2) * omcos, axis.getX() * axis.getY() * omcos - axis.getZ() * sin, axis.getX() * axis.getZ() * omcos + axis.getY() * sin, 0.0,
                axis.getY() * axis.getX() * omcos + axis.getZ() * sin, cos + Math.pow(axis.getY(), 2) * omcos, axis.getY() * axis.getZ() * omcos - axis.getX() * sin, 0.0,
                axis.getZ() * axis.getX() * omcos - axis.getY() * sin, axis.getZ() * axis.getY() * omcos + axis.getX() * sin, cos + Math.pow(axis.getZ(), 2) * omcos, 0.0,
                0.0, 0.0, 0.0, 1.0
        }));
    }

    /**
     * Returns the translated version of the given matrix
     * @param mat The source matrix
     * @param t The translation vector
     * @return The resulting matrix
     */
    public static Mat4 translate(Mat4 mat, Vec3 t)
    {
        return mat.multiply(new Mat4(new double[] {
                1.0, 0.0, 0.0, t.getX(),
                0.0, 1.0, 0.0, t.getY(),
                0.0, 0.0, 1.0, t.getZ(),
                0.0, 0.0, 0.0, 1.0
        }));
    }

    /**
     * Creates a perspective projection matrix
     * @param fovY Field of view
     * @param aspect Aspect ratio
     * @param near Near plain's distance
     * @param far Far plain's distance
     * @return The projection matrix
     */
    public static Mat4 perspective(double fovY, double aspect, double near, double far)
    {
        double t = Math.tan(Math.toRadians(fovY / 2.0)) * near;
        double r = t * aspect;

        return new Mat4(new double[] {
                near / r, 0.0,       0.0,                          0.0,
                0.0,      near / t,  0.0,                          0.0,
                0.0,      0.0,      -(far + near) / (far - near), -2 * far * near / (far - near),
                0.0,      0.0,      -1.0,                          0.0
        });
    }

    /**
     * Creates an orthographic projection matrix
     * @param left Left plain's position
     * @param right Right plain's position
     * @param top Top plain's position
     * @param bottom Bottom plain's position
     * @param near Near plain's distance
     * @param far Far plain's distance
     * @return The projection matrix
     */
    public static Mat4 orthographic(double left, double right, double top, double bottom, double near, double far)
    {
        return new Mat4(new double[] {
                2.0 / (right - left), 0.0,                   0.0,                -(right + left) / (right - left),
                0.0,                  2.0 / (top - bottom),  0.0,                -(top + bottom) / (top - bottom),
                0.0,                  0.0,                  -2.0 / (far - near), -(far + near) / (far - near),
                0.0,                  0.0,                   0.0,                 1.0
        });
    }

    /**
     * Calculates to world->view transformation matrix
     * @param position The position of the viewer
     * @param direction The facing direction of the viewer
     * @param up The up direction of the viewer
     * @return The transformation matrix
     */
    public static Mat4 lookAt(Vec3 position, Vec3 direction, Vec3 up)
    {
        // calculate the view space basis vectors
        direction = direction.normalize();
        Vec3 right = direction.cross(up).normalize();
        up = right.cross(direction).normalize();
        direction = direction.negate();

        /*
           basis transformation from world->view is done using the inverse of the view->world matrix
           the view->world matrix is (b1|b2|b3|e4) where bi-s are the basis vectors, e4 is (0,0,0,1)
           to get world->view from view->world, we have to calculate the inverse, which is just the
           transpose of the matrix, because it is orthonormal (columns are orthogonal and normalized)
        */
        Mat4 worldToView = new Mat4(new double[] {
                right.getX(),     right.getY(),     right.getZ(),     0.0,
                up.getX(),        up.getY(),        up.getZ(),        0.0,
                direction.getX(), direction.getY(), direction.getZ(), 0.0,
                0.0,              0.0,              0.0,              1.0
        });

        // translate to the position
        worldToView = Meth.translate(worldToView, position.negate());

        return worldToView;
    }

    /**
     * Returns if the value v is between min and max (int version)
     */
    public static boolean isBetween(int v, int min, int max) { return min <= v && v <= max; }

    /**
     * Returns if the value v is between min and max (double version)
     */
    public static boolean isBetween(double v, double min, double max)
    {
        return min <= v && v <= max;
    }
}
