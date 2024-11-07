import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestMain extends Canvas implements Runnable
{
    private static final int WIDTH = 1800;
    private static final int HEIGHT = 1200;

    private static final int FPS = 60;
    private static final int FRAME_TIME = 1_000_000_000 / FPS;

    private boolean windowShouldClose = false;

    private Camera cam;
    private List<Cube> cubes;

    public TestMain()
    {
        JFrame frame = new JFrame("Escape KSI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.pack();
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // double buffering
        this.createBufferStrategy(2);
    }

    public void start()
    {
        cam = new Camera(45.0, (double)WIDTH / HEIGHT, 0.1, 100.0);
        cam.setMouseLock(true);
        Renderer.init(this);
        Input.init(this);

        cubes = new ArrayList<>();
        cubes.add(new Cube(new Vec3(0,-0.5, -5.0), new Vec3(1.0, 1.5, 1.0), new Vec3(0.0)));

        new Thread(this).start();
    }

    @Override
    public void run()
    {
        double dt = 0.0;
        while (!windowShouldClose)
        {
            long startTime = System.nanoTime();

            Input.update();
            cam.update(dt);
            render();

            dt = (System.nanoTime() - startTime) / 1_000_000_000.0;

            if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) System.exit(0);
        }
    }

    private void render()
    {
        Graphics2D g2d = (Graphics2D) getBufferStrategy().getDrawGraphics();

        for (Cube cube : cubes)
            Renderer.add(cube);
        Renderer.render(cam);

        g2d.setColor(Color.yellow);
        Point mousePos = getMousePosition();
        if (mousePos != null)
            g2d.fillOval(mousePos.x, mousePos.y, 10, 10);

        g2d.dispose();
        getBufferStrategy().show();
    }

    public static void main(String[] args)
    {
        TestMain game = new TestMain();
        game.start();
    }
}