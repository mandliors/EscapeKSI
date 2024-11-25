package main.java.meth;

import java.io.Serializable;

public class Vec2 implements Serializable
{
    /**
     * x coordinate of the vector
     */
    private double x;
    /**
     * y coordinate of the vector
     */
    private double y;

    /**
     * Construct a vector with the given x and y
     */
    public Vec2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Construct a vector where both x and y are equal to the given parameter
     */
    public Vec2(double v) { this.x = this.y = v; }

    /**
     * Sets the x value of the vector
     */
    public void setX(double x) { this.x = x; }
    /**
     * Sets the y value of the vector
     */
    public void setY(double y) { this.y = y; }


    /**
     * Returns the x value of the vector
     */
    public double getX() { return x; }
    /**
     * Returns the y value of the vector
     */
    public double getY() { return y; }
    /**
     * Returns the length of the vector
     */
    public double getLength() { return Math.sqrt(x * x + y * y); }

    /**
     * Adds two vectors together (does not modify them)
     * @return The resulting vector
     */
    public Vec2 add(Vec2 other) { return new Vec2(x + other.getX(), y + other.getY()); }
    /**
    * Subtracts a vector from another (does not modify them)
    * @return The resulting vector
    */
    public Vec2 subtract(Vec2 other) { return new Vec2(x - other.getX(), y - other.getY()); }
    /**
     * Returns the scaled version of the vector (does not modify it)
     * @return The resulting vector
     */
    public Vec2 scale(double scalar) { return new Vec2(x * scalar, y * scalar); }
    /**
     * Returns the distance between the positions defined by the vector
     */
    public double distance(Vec2 other) { return this.subtract(other).getLength(); }
}
