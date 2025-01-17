package main.java.meth;

import java.io.Serializable;

public class Mat4 implements Serializable
{
    /**
     * Matrix data
     */
    double[] data;

    /**
     * Creates a zeroed out 4x4 matrix
     */
    public Mat4() { this(0.0); }

    /**
     * Creates a 4x4 matrix where everything is zero, except the diagonal
     * @param v The value in the diagonal
     */
    public Mat4(double v)
    {
        data = new double[16];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                set(i, j, i == j ? v : 0);
    }

    /**
     * Creates a 4x4 matrix using the given raw data
     */
    public Mat4(double[] data)
    {
        this.data = new double[16];
        System.arraycopy(data, 0, this.data, 0, 16);
    }

    /**
     * Returns the element at the given index
     */
    public double get(int i, int j) { return data[i * 4 + j]; }

    /**
     * Sets the element at the given index
     */
    public void set(int i, int j, double v) { data[i * 4 + j] = v; }

    /**
     * Adds two matrices together (does not modify them)
     * @return The resulting matrix
     */
    public Mat4 add(Mat4 other)
    {
        Mat4 res = new Mat4();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                res.set(i, j, get(i, j) + other.get(i, j));
        return res;
    }

    /**
     * Multiplies matrices (does not modify them)
     * @return The resulting matrix
     */
    public Mat4 multiply(Mat4 other)
    {
        Mat4 res = new Mat4(0.0);
        for (int i = 0; i < 4; i++) // row
            for (int j = 0; j < 4; j++) // column
                for (int k = 0; k < 4; k++) // item
                    res.data[i * 4 + j] += data[i * 4 + k] * other.data[k * 4 + j];
        return res;
    }

    /**
     * Multiplies a 4x4 matrix by a 4-dim vector (does not modify them)
     * @return The resulting vector
     */
    public Vec4 multiply(Vec4 vec)
    {
        return new Vec4(
                data[0]  * vec.getX() + data[1]  * vec.getY() + data[2]  * vec.getZ() + data[3]  * vec.getW(),
                data[4]  * vec.getX() + data[5]  * vec.getY() + data[6]  * vec.getZ() + data[7]  * vec.getW(),
                data[8]  * vec.getX() + data[9]  * vec.getY() + data[10] * vec.getZ() + data[11] * vec.getW(),
                data[12] * vec.getX() + data[13] * vec.getY() + data[14] * vec.getZ() + data[15] * vec.getW()
        );
    }

    /**
     * Returns the transpose of the matrix (does not modify it)
     */
    public Mat4 transpose()
    {
        Mat4 res = new Mat4();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                res.set(i, j, get(j, i));
        return res;
    }

    /**
     * Prints the matrix in a nice format
     */
    public void print()
    {
        for (int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
                System.out.print(data[i * 4 + j] + " ");
            System.out.println();
        }
    }
}
