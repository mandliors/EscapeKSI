package test.java;

import main.java.meth.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vec4Test
{
    /**
     * Error margin for double comparison
     */
    static final double ERROR_MARGIN = 1e-8;
    /**
     * Two vectors for the tests
     */
    static Vec3 a, b;

    /**
     * Setups, runs once before the tests
     */
    @BeforeAll
    static void setUp()
    {
        a = new Vec3(3.0, -4.0, 5.0);
        b = new Vec3(0.0, -2.0, 7.0);
    }

    /**
     * Function that tests vector addition
     */
    @Test
    void testAdd()
    {
        Vec3 sum = a.add(b);

        assertEquals(3.0, sum.getX(), ERROR_MARGIN);
        assertEquals(-6.0, sum.getY(), ERROR_MARGIN);
        assertEquals(12.0, sum.getZ(), ERROR_MARGIN);
    }

    /**
     * Function that tests vector dot product
     */
    @Test
    void testDot()
    {
        assertEquals(43.0, a.dot(b), ERROR_MARGIN);
    }

    /**
     * Function that test vector length function
     */
    @Test
    void testGetLength()
    {
        assertEquals(Math.sqrt(50), a.getLength(), ERROR_MARGIN);
    }

    /**
     * Function that tests the normalize function
     */
    @Test
    void testNormalize()
    {
        Vec3 normalized = a.normalize();
        double length = Math.sqrt(50);

        assertEquals(3.0 / length, normalized.getX(), ERROR_MARGIN);
        assertEquals(-4.0 / length, normalized.getY(), ERROR_MARGIN);
        assertEquals(5.0 / length, normalized.getZ(), ERROR_MARGIN);
    }
}