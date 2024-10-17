import java.awt.*;
import java.util.*;
import java.util.List;

public class Renderer
{
    private int width;
    private int height;

    private Mat4 projection;

    private static final List<Trigon> trigons = new ArrayList<Trigon>();

    public Renderer(int width, int height)
    {
        this.width = width;
        this.height = height;

        projection = Meth.perspective(45.0, (double)width / height, 0.1, 100.0);
        //projection = Meth.orthographic(-10,10,10,-10,0,10);
    }

    public void submitTrigon(Trigon trigon)
    {
        trigons.add(trigon);
    }

    public void renderTrigons(Graphics g, Mat4 view)
    {
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(width / 2, height / 2);

        for (Trigon t : trigons)
        {
            //proj * view * model * pos
            Vec4[] points = new Vec4[3];
            for (int i = 0; i < 3; i++)
            {
                points[i] = projection.mul(view.mul(t.getModelMatrix().mul(new Vec4(t.getPoints()[i], 1.0))));
                points[i].setX(points[i].getX() / points[i].getW());
                points[i].setY(points[i].getY() / points[i].getW());
                points[i].setZ(points[i].getZ() / points[i].getW());
            }


            double scaleH = width / 2.0;
            double scaleV = height / 2.0;
            g2d.setColor(t.getColor());
            g2d.fillPolygon(
                    new int[]{(int) (points[0].getX() * scaleH), (int) (points[1].getX() * scaleH), (int) (points[2].getX() * scaleH) },
                    new int[]{(int) (points[0].getY() * scaleV), (int) (points[1].getY() * scaleV), (int) (points[2].getY() * scaleV) }, 3
            );
        }
    }
}
