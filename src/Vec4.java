public class Vec4
{
    private double x, y, z, w;

    public Vec4(double x, double y, double z, double w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public Vec4(double v)
    {
        this.x = this.y = this.z = this.w = v;
    }
    public Vec4(Vec3 v, double w)
    {
        this.x = v.getX();
        this.y = v.getY();
        this.z = v.getZ();
        this.w = w;
    }

    public Vec3 xyz() { return new Vec3(x, y, z); }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public double getW() { return w; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }
    public void setW(double w) { this.w = w; }

    public Vec4 add(Vec4 other) { return new Vec4(x + other.getX(), y + other.getY(), z + other.getZ(), w + other.getW()); }
    public Vec4 subtract(Vec4 other) { return add(other.negate()); }
    public Vec4 negate() { return scale(-1); }
    public Vec4 scale(double scalar) { return new Vec4(x * scalar, y * scalar, z * scalar, w * scalar); }

    public void print() { System.out.printf("%g %g %g %g\n", x, y, z, w); }
}
