package test.java;

import main.java.meth.Mat4;
import main.java.meth.Vec4;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Mat4Test
{
    static final double ERROR_MARGIN = 1e-8;
    static Mat4 identity;
    static Mat4 mat;
    static Vec4 vec;

    @BeforeAll
    static void setUp()
    {
        identity = new Mat4(1.0);

        mat = new Mat4(new double[] {
                 1.0, 2.0,  0.0, -1.0,
                -3.0, 4.0,  0.5,  0.0,
                -2.0, 0.0, -0.5,  4.0,
                 0.0, 1.0,  1.0, -3.0
        });

        vec = new Vec4(0.0, 1.0, 0.0, -1.0);
    }

    @Test
    void oneValueConstructorTest()
    {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if (i == j) assertEquals(1.0, identity.get(i, j), ERROR_MARGIN);
                else assertEquals(0.0, identity.get(i, j), ERROR_MARGIN);
            }
        }
    }

    @Test
    void addTest()
    {
        Mat4 sum = mat.add(identity);
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                assertEquals(mat.get(i, j) + identity.get(i, j), sum.get(i, j), ERROR_MARGIN);
    }

    @Test
    void multiplyWithMatrixTest()
    {
        Mat4 product = mat.multiply(identity);
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                assertEquals(mat.get(i, j), product.get(i, j), ERROR_MARGIN);
    }

    @Test
    void multiplyWithVec4Test()
    {
        Vec4 product = mat.multiply(vec);

        assertEquals(3.0, product.getX(), ERROR_MARGIN);
        assertEquals(4.0, product.getY(), ERROR_MARGIN);
        assertEquals(-4.0, product.getZ(), ERROR_MARGIN);
        assertEquals(4.0, product.getW(), ERROR_MARGIN);
    }
}