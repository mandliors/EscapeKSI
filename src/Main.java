import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Main extends Canvas implements Runnable
{
    private static final int WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final int HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private static final int FPS = 60;
    private static final int FRAME_TIME = 1_000_000_000 / FPS;

    private boolean windowShouldClose = false;

    private Camera cam;

    private Tetrahedron tetra;
    private Cube cube;

    public Main()
    {
        JFrame frame = new JFrame("Escape KSI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add(this);
        frame.pack();
        //frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // double buffering
        this.createBufferStrategy(2);
    }

    public void start()
    {
        cam = new Camera(45.0, (double)WIDTH / HEIGHT, 0.1, 100.0);
        Renderer.init(this);
        Input.init(this);
        //cam.setMouseLock(true);

        tetra = new Tetrahedron(new Vec3(0.5, 0.5, -5.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0));
        cube = new Cube(new Vec3(-0.5, 0, -5.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0));

        new Thread(this).start();
    }

    @Override
    public void run()
    {
        Font bigFont = new Font("Monospaced", Font.BOLD, 40);

        double dt = 0.0;
        while (!windowShouldClose)
        {
            long startTime = System.nanoTime();

            Input.update();
            cam.update(dt);
            tetra._rotate(dt);
            cube._rotate(dt);
            render();

            dt = (System.nanoTime() - startTime) / 1_000_000_000.0;

            Graphics2D g2d = (Graphics2D) getGraphics();
            g2d.setColor(Color.GREEN);
            g2d.setFont(bigFont);
            g2d.drawString(String.format("%d FPS", (int)(1 / dt)), 10, 50);

            if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) System.exit(0);
        }
    }

    private void render()
    {
        Graphics2D g2d = (Graphics2D) getBufferStrategy().getDrawGraphics();

        Renderer.add(cube);
        Renderer.add(tetra);
        Renderer.render(cam);

        g2d.setColor(Color.yellow);
        Point mousePos = getMousePosition();
        if (mousePos != null)
            g2d.fillOval(mousePos.x - 5, mousePos.y - 5, 10, 10);

        g2d.dispose();
        getBufferStrategy().show();
    }

    public static void main(String[] args)
    {
        Main game = new Main();
        game.start();
    }
}