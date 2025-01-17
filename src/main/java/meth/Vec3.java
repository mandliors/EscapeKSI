package main.java.meth;

import java.io.Serializable;

public class Vec3 implements Serializable
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
     * Construct a vector with the given x, y and z
     */
    public Vec3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    /**
     * Construct a vector where x, y and z are equal to the given parameter
     */
    public Vec3(double v) { this.x = this.y = this.z = v; }
    /**
     * Construct a vector with the first three coordinates of a 4 dimensional vector
     */
    public Vec3(Vec4 v)
    {
        this.x = v.getX();
        this.y = v.getY();
        this.z = v.getZ();
    }

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
     * Returns the length of the vector
     */
    public double getLength() { return Math.sqrt(getLengthSquared()); }
    /**
     * Returns the length of the vector squared
     */
    public double getLengthSquared() { return x * x + y * y + z * z; }

    /**
     * Adds two vectors together (does not modify them)
     * @return The resulting vector
     */
    public Vec3 add(Vec3 other) { return new Vec3(x + other.getX(), y + other.getY(), z + other.getZ()); }
    /**
     * Subtracts a vector from another (does not modify them)
     * @return The resulting vector
     */
    public Vec3 subtract(Vec3 other) { return add(other.negate()); }

    /**
     * Multiplies two vectors together component-wise (does not modify them)
     * @return The resulting vector
     */
    public Vec3 multiply(Vec3 other) { return new Vec3(x * other.getX(), y * other.getY(), z * other.getZ()); }

    /**
     * Returns the negated version of the vector (does not modify it)
     * @return The resulting vector
     */
    public Vec3 negate() { return scale(-1); }
    /**
     * Returns the scaled version of the vector (does not modify it)
     * @return The resulting vector
     */
    public Vec3 scale(double scalar) { return new Vec3(x * scalar, y * scalar, z * scalar); }
    /**
     * Returns the normalized version of the vector (does not modify it)
     * @return The resulting vector
     */
    public Vec3 normalize() { double length = getLength(); return new Vec3(x / length, y / length, z / length); }

    /**
     * Returns the dot product of two vectors
     */
    public double dot(Vec3 other) { return x * other.getX() + y * other.getY() + z * other.getZ(); }
    /**
     * Returns the cross product of two vectors
     */
    public Vec3 cross(Vec3 other) { return new Vec3(y * other.getZ() - z * other.getY(), z * other.getX() - x * other.getZ(), x * other.getY() - y * other.getX()); }

    /**
     * Returns a vector of the x and y components
     */
    public Vec2 xy() { return new Vec2(x, y); }
    /**
     * Returns a vector of the x and z components
     */
    public Vec2 xz() { return new Vec2(x, z); }
    /**
     * Returns a vector of the y and z components
     */
    public Vec2 yz() { return new Vec2(y, z); }

    /**
     * Prints the vector in a nice format
     */
    public void print() { System.out.printf("%g %g %g\n", x, y, z); }
}
