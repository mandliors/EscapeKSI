package test.java;

import main.java.collision.Collision;
import main.java.gameobjects.shapes.ColoredCube;
import main.java.meth.Vec3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;

class CollisionTest
{
    /**
     * Error margin for double comparison
     */
    static final double ERROR_MARGIN = 1e-8;
    /**
     * A cube for the tests
     */
    ColoredCube cube1;
    /**
     * Another cube for the tests
     */
    ColoredCube cube2;

    /**
     * Setup that runs before each test
     */
    @BeforeEach
    void setUp()
    {
        cube1 = new ColoredCube(new Vec3(0.0, 0.0, 0.0), new Vec3(3.0, 1.0, 2.0), new Vec3(0.0), Color.WHITE);
        cube2 = new ColoredCube(new Vec3(1.8, 0.3, -1.0), new Vec3(2.0, 1.0, 1.0), new Vec3(0.0), Color.WHITE);
    }

    /**
     * Tests collision between two gameobjects (they do collide)
     */
    @Test
    void testCollision()
    {
        assertTrue(Collision.collideWith(cube1, cube2));
    }

    /**
     * Tests collision between two gameobjects (they don't collide)
     */
    @Test
    void testCollision2()
    {
        cube2.translate(new Vec3(10.0));
        assertFalse(Collision.collideWith(cube1, cube2));
    }

    /**
     * Tests the function that calculates the displacement vector for a specific collision
     */
    @Test
    void testCollisionDisplacement()
    {
        Vec3 collisionDisplacement = new Collision(cube1, cube2).getDisplacement();
        assertEquals(0.0, collisionDisplacement.getX(), ERROR_MARGIN);
        assertEquals(0.0, collisionDisplacement.getY(), ERROR_MARGIN);
        assertEquals(0.5 + 2 * Collision.getColliderPadding(), collisionDisplacement.getZ(), ERROR_MARGIN);
    }
}