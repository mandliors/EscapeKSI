import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MainFrame extends JFrame
{
    private final int WIDTH = 1800;
    private final int HEIGHT = 1200;

    Container cont;
    JPanel canvas;

    public MainFrame()
    {
        super("Escape KSI");

        cont = getContentPane();
        cont.setLayout(new BorderLayout());

        Camera camera = new Camera();
        Renderer renderer = new Renderer(WIDTH, HEIGHT);

        Trigon t1 = new Trigon(
                new Vec3(0.5,  0.5, 0.0),
                new Vec3(0.5, -0.5, 0.0),
                new Vec3(-0.5,  0.5, 0.0),
                Color.CYAN
        );
        Trigon t2 = new Trigon(
                new Vec3(0.5, -0.5, 0.0),
                new Vec3(-0.5, -0.5, 0.0),
                new Vec3(-0.5,  0.5, 0.0),
                Color.RED
        );

        renderer.submitTrigon(t1);
        renderer.submitTrigon(t2);

        canvas = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.darkGray);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                renderer.renderTrigons(g, camera.getViewMatrix());
            }
        };
        cont.add(canvas, BorderLayout.CENTER);

        setSize(WIDTH, HEIGHT);
        setLocation(500, 300);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args)
    {
        new MainFrame();
    }
}