package main.java.meth;

import java.io.Serializable;

public class Vec4 implements Serializable
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
     * z coordinate of the vector
     */
    private double z;
    /**
     * w coordinate of the vector
     */
    private double w;

    /**
     * Construct a vector with the given x, y, z and w
     */
    public Vec4(double x, double y, double z, double w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    /**
     * Construct a vector where x, y, z and w are equal to the given parameter
     */
    public Vec4(double v) { this.x = this.y = this.z = this.w = v; }
    /**
     * Construct a vector where x, y and z is initialized with the given vector, and the w component is the given value
     */
    public Vec4(Vec3 v, double w)
    {
        this.x = v.getX();
        this.y = v.getY();
        this.z = v.getZ();
        this.w = w;
    }

    /**
     * Returns a vector of the first three components
     */
    public Vec3 xyz() { return new Vec3(x, y, z); }


    /**
     * Returns the x value of the vector
     */
    public double getX() { return x; }
    /**
     * Returns the y value of the vector
     */
    public double getY() { return y; }
    /**
     * Returns the z value of the vector
     */
    public double getZ() { return z; }
    /**
     * Returns the w value of the vector
     */
    public double getW() { return w; }

    /**
     * Sets the x value of the vector
     */
    public void setX(double x) { this.x = x; }
    /**
     * Sets the y value of the vector
     */
    public void setY(double y) { this.y = y; }
    /**
     * Sets the z value of the vector
     */
    public void setZ(double z) { this.z = z; }
    /**
     * Sets the w value of the vector
     */
    public void setW(double w) { this.w = w; }

    /**
     * Adds two vectors together (does not modify them)
     * @return The resulting vector
     */
    public Vec4 add(Vec4 other) { return new Vec4(x + other.getX(), y + other.getY(), z + other.getZ(), w + other.getW()); }
    /**
     * Subtracts a vector from another (does not modify them)
     * @return The resulting vector
     */
    public Vec4 subtract(Vec4 other) { return add(other.negate()); }
    /**
     * Returns the negated version of the vector (does not modify it)
     * @return The resulting vector
     */
    public Vec4 negate() { return scale(-1); }
    /**
     * Returns the scaled version of the vector (does not modify it)
     * @return The resulting vector
     */
    public Vec4 scale(double scalar) { return new Vec4(x * scalar, y * scalar, z * scalar, w * scalar); }

    /**
     * Prints the vector in a nice format
     */
    public void print() { System.out.printf("%g %g %g %g\n", x, y, z, w); }
}
