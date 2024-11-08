public class ObjectData
{
    private double[] vertices;
    private short[] indices;

    public double[] getVertices() { return vertices; }
    public short[] getIndices() { return indices; }

    public ObjectData(double[] vertices, short[] indices)
    {
        this.vertices = vertices;
        this.indices = indices;
    }
}
