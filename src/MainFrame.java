import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainFrame extends JFrame
{
    Container cont;
    JPanel canvas;
    ArrayList<Trigon> trigons;

    public MainFrame()
    {
        super("Escape KSI");

        cont = getContentPane();
        cont.setLayout(new BorderLayout());

        trigons = new ArrayList<>();
        trigons.add(new Trigon(
                new Vec3(0.5,  0.5, 0.0),
                new Vec3(0.5, -0.5, 0.0),
                new Vec3(-0.5,  0.5, 0.0),
                Color.CYAN)
        );
        trigons.add(new Trigon(
                new Vec3(0.5, -0.5, 0.0),
                new Vec3(-0.5, -0.5, 0.0),
                new Vec3(-0.5,  0.5, 0.0),
                Color.RED)
        );

        canvas = new JPanel() { public void paintComponent(Graphics g) { render(g); } };
        cont.add(canvas, BorderLayout.CENTER);

        setSize(1800,1200);
        setLocation(500, 300);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void render(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.translate(getWidth() / 2, getHeight() / 2);

        for (Trigon t : trigons)
            drawTrigon(g2d, t);
    }

    private void drawTrigon(Graphics2D g2d, Trigon t)
    {
        double scaleH = getWidth() / 2;
        double scaleV = getHeight() / 2;

        g2d.setColor(t.getColor());
        g2d.fillPolygon(
                new int[]{(int) (t.getP1().getX() * scaleH), (int) (t.getP2().getX() * scaleH), (int) (t.getP3().getX() * scaleH) },
                new int[]{(int) (t.getP1().getY() * scaleV), (int) (t.getP2().getY() * scaleV), (int) (t.getP3().getY() * scaleV) }, 3
        );
    }

    public static void main(String[] args)
    {
        new MainFrame();
    }
}