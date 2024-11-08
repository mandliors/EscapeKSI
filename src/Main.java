import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Main extends Canvas implements Runnable
{
    private static final int WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final int HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private static final int MAX_FPS = 120;
    private static final double FRAME_TIME = 1.0 / MAX_FPS;

    private boolean windowShouldClose = false;
    private double dt = 0.0;

    private Camera cam;

    private Tetrahedron tetra;
    private ColoredCube cube;
    private ColoredCube cube2;

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
        cam.setMouseLock(true);

        tetra = new Tetrahedron(new Vec3(0.5, 0.5, -5.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0), Color.CYAN);
        cube = new ColoredCube(new Vec3(-0.5, 0, -5.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0), Color.ORANGE);
        cube2 = new ColoredCube(new Vec3(-0.1, -0.4, -5.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0), Color.ORANGE);
        Renderer.addRenderable(new ColoredCube(new Vec3(-0.2, 0.1, -5.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0), Color.ORANGE));
        Renderer.addRenderable(new ColoredCube(new Vec3( 0.4, 0.4, -6.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0), Color.ORANGE));
        Renderer.addRenderable(new ColoredCube(new Vec3(-0.4, 0.2, -7.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0), Color.ORANGE));
        Renderer.addRenderable(new ColoredCube(new Vec3( 0.8, -0.5, -4.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0), Color.ORANGE));
        Renderer.addRenderable(new ColoredCube(new Vec3( 0.6, -0.1, -3.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0), Color.ORANGE));

        new Thread(this).start();
    }

    @Override
    public void run()
    {
        Renderer.addRenderable(cube);
        Renderer.addRenderable(cube2);
        Renderer.addRenderable(tetra);

        while (!windowShouldClose)
        {
            long startTime = System.nanoTime();

            Input.update();
            cam.update(dt);
            tetra.rotate(new Vec3(0.0, 30 * dt, 0.0));
            cube.rotate(new Vec3(0.0, 80 * dt, 0.0));
            cube2.rotate(new Vec3(0.0, 100 * dt, 0.0));
            render();

            dt = (System.nanoTime() - startTime) / 1_000_000_000.0;
            if (dt < FRAME_TIME)
            {
                try { Thread.sleep((long)(1000 * (FRAME_TIME - dt))); } catch (Exception e) { e.printStackTrace(); }
                dt = FRAME_TIME;
            }

            if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) System.exit(0);
        }
    }

    Font bigFont = new Font("Monospaced", Font.BOLD, 40);
    private void render()
    {
        Graphics2D g2d = (Graphics2D) getBufferStrategy().getDrawGraphics();

        Renderer.render(g2d, cam);

        g2d.setColor(Color.yellow);
        Point mousePos = getMousePosition();
        if (mousePos != null)
            g2d.fillOval(mousePos.x - 5, mousePos.y - 5, 10, 10);

        g2d.setColor(Color.GREEN);
        g2d.setFont(bigFont);
        g2d.drawString(String.format("%d FPS", (int)(1.0 / dt)), 10, 50);

        g2d.dispose();
        getBufferStrategy().show();
    }

    public static void main(String[] args)
    {
        Main game = new Main();
        game.start();
    }
}