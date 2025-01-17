package test.java;

import main.java.meth.Mat4;
import main.java.meth.Vec4;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Mat4Test
{
    /**
     * Error margin for double comparison
     */
    static final double ERROR_MARGIN = 1e-8;
    /**
     * Identity matrix, needed for the tests
     */
    static Mat4 identity;
    /**
     * A matrix for the tests
     */
    static Mat4 mat;
    /**
     * A vector for the tests
     */
    static Vec4 vec;

    /**
     * Function that sets up the data for the tests
     */
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

    /**
     * Tests the one value constructor of the matrix
     */
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

    /**
     * Tests the addition function on matrices
     */
    @Test
    void addTest()
    {
        Mat4 sum = mat.add(identity);
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                assertEquals(mat.get(i, j) + identity.get(i, j), sum.get(i, j), ERROR_MARGIN);
    }

    /**
     * Tests the multiplication on 2 matrices (mat * identity)
     */
    @Test
    void multiplyWithMatrixTest()
    {
        Mat4 product = mat.multiply(identity);
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                assertEquals(mat.get(i, j), product.get(i, j), ERROR_MARGIN);
    }

    /**
     * Tests the matrix * vector function
     */
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