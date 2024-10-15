import java.awt.*;
import java.util.*;
import java.util.List;

public class Renderer
{
    private Mat4 projection;
    private static final List<Trigon> trigons = new ArrayList<Trigon>();

    public Renderer(int width, int height)
    {
        projection = Meth.perspective(45.0, (double)width / height, 0.1, 100.0);
    }

    public void submitTrigon(Trigon trigon)
    {
        trigons.add(trigon);
    }

    public void renderTrigons(Graphics g)
    {
        // ...
    }
}
