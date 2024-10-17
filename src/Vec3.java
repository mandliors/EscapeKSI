public class Vec3
{
    private double x, y, z;

    public Vec3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vec3(double v)
    {
        this.x = this.y = this.z = v;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    public double getLength() { return Math.sqrt(getLengthSqr()); }
    public double getLengthSqr() { return x * x + y * y + z * z; }

    public Vec3 add(Vec3 other) { return new Vec3(x + other.getX(), y + other.getY(), z + other.getZ()); }
    public Vec3 sub(Vec3 other) { return add(other.neg()); }
    public Vec3 neg() { return mul(-1); }
    public Vec3 mul(double scalar) { return new Vec3(x * scalar, y * scalar, z * scalar); }
    public Vec3 normalize() { double length = getLength(); return new Vec3(x / length, y / length, z / length); }

    public double dot(Vec3 other) { return x * other.getX() + y * other.getY() + z * other.getZ(); }
    public Vec3 cross(Vec3 other) { return new Vec3(y * other.getZ() - z * other.getY(), z * other.getX() - x * other.getZ(), x * other.getY() - y * other.getX()); }
}
