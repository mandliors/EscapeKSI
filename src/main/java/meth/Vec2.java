package main.java.meth;

import java.io.Serializable;

public class Vec2 implements Serializable
{
    private double x, y;

    public Vec2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    public Vec2(double v) { this.x = this.y = v; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getLength() { return Math.sqrt(x * x + y * y); }

    public Vec2 add(Vec2 other) { return new Vec2(x + other.getX(), y + other.getY()); }
    public Vec2 subtract(Vec2 other) { return new Vec2(x - other.getX(), y - other.getY()); }
    public Vec2 scale(double scalar) { return new Vec2(x * scalar, y * scalar); }
    public double distance(Vec2 other) { return this.subtract(other).getLength(); }
}
