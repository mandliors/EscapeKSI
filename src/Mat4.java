public class Mat4
{
    double data[];

    public Mat4() { this(0.0); }
    public Mat4(double v)
    {
        data = new double[16];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                set(i, j, i == j ? v : 0);
    }
    public Mat4(double[] data) { this.data = data; }

    public double get(int i, int j) { return data[i * 4 + j]; }
    public void set(int i, int j, double v) { data[i * 4 + j] = v; }

    public Mat4 add(Mat4 other)
    {
        Mat4 res = new Mat4();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                res.set(i, j, get(i, j) + other.get(i, j));
        return res;
    }

    public Mat4 mul(Mat4 other)
    {
        Mat4 res = new Mat4();
        for (int i = 0; i < 16; i += 4)
        {
            for (int j = 0; j < 4; j++)
            {
                res.data[i + j] = 0.0;
                for (int k = 0; k < 4; k++)
                    res.data[i + j] += data[i + k] * other.data[k * 4 + j];
            }
        }
        return res;
    }
}
