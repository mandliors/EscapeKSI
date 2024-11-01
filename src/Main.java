import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;

public class Main extends Canvas implements Runnable
{
    private static final int WIDTH = 1800;
    private static final int HEIGHT = 1200;

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
        frame.add(this);
        frame.pack();
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // triple buffering
        this.createBufferStrategy(2);
    }

    public void start()
    {
        cam = new Camera(45.0, (double)WIDTH / HEIGHT, 0.1, 100.0);
        Renderer.init(WIDTH, HEIGHT);

        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Input.setKeyDown(e.getKeyCode(), true);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                Input.setKeyDown(e.getKeyCode(), false);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { Input.setMouseButtonDown(e.getButton(), true); }
            @Override
            public void mouseReleased(MouseEvent e) { Input.setMouseButtonDown(e.getButton(), false); }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) { Input.addMouseDelta(new Vec2(e.getX(), e.getY())); }
        });

        tetra = new Tetrahedron(new Vec3(0.5, 0.5, -5.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0));
        cube = new Cube(new Vec3(-0.5, 0, -5.0), new Vec3(0.5), new Vec3(30.0, 20.0, 10.0));

        new Thread(this).start();
    }

    @Override
    public void run()
    {
        double dt = 0.0;
        while (!windowShouldClose)
        {
            long startTime = System.nanoTime();

            update(dt);
            render();

            long elapsed = System.nanoTime() - startTime;
            long waitTime = (FRAME_TIME - elapsed) / 1_000_000;
            dt = elapsed / 1_000_000_000.0;
            if (waitTime > 0)
            {
                try { Thread.sleep(waitTime); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }

    private void update(double dt)
    {
        short dx = 0, dz = 0;
        if (Input.isKeyDown(KeyEvent.VK_W)) dz += -1;
        if (Input.isKeyDown(KeyEvent.VK_A)) dx += -1;
        if (Input.isKeyDown(KeyEvent.VK_S)) dz += 1;
        if (Input.isKeyDown(KeyEvent.VK_D)) dx += 1;
        cam.translate(new Vec2(dx * dt, dz * dt));
        //cam.rotate(Input.getMouseDelta().scale(dt * 0.1));

        tetra._rotate();
        cube._rotate();
    }

    private void render()
    {
        Graphics2D g2d = (Graphics2D) getBufferStrategy().getDrawGraphics();

        Renderer.add(cube);
        Renderer.add(tetra);
        Renderer.render(g2d, cam.getProjection(), cam.getView());

        g2d.setColor(Color.yellow);
        if (getMousePosition() != null)
            g2d.fillOval(getMousePosition().x - 5, getMousePosition().y - 5, 10, 10);

        getBufferStrategy().show();
    }

    public static void main(String[] args)
    {
        Main game = new Main();
        game.start();
    }
}