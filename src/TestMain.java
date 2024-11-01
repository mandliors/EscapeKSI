import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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

        // triple buffering
        this.createBufferStrategy(2);
    }

    public void start()
    {
        cam = new Camera(90.0, (double)WIDTH / HEIGHT, 0.1, 100.0);
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

        cubes = new LinkedList<>();
        cubes.add(new Cube(new Vec3(0,-0.5, -5.0), new Vec3(1.0, 1.5, 1.0), new Vec3(0.0)));

//        cubes.add(new Cube(new Vec3(0,0.5, 10.0), new Vec3(2.0, 0.2, 2.0), new Vec3(0.0)));
//        cubes.add(new Cube(new Vec3(0,-0.5, 10.0), new Vec3(2.0, 0.2, 2.0), new Vec3(0.0)));
//        for (double x = -1.0; x < 1.1; x += 0.2)
//            cubes.add(new Cube(new Vec3(x,0.0, 15.0), new Vec3(0.05, 0.8, 1.0), new Vec3(0.0)));


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

            dt = (System.nanoTime() - startTime) / 1_000_000_000.0;
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
    }

    private void render()
    {
        Graphics2D g2d = (Graphics2D) getBufferStrategy().getDrawGraphics();

        for (Cube cube : cubes)
            Renderer.add(cube);
        Renderer.render(g2d, cam.getProjection(), cam.getView());

        g2d.setColor(Color.yellow);
        if (getMousePosition() != null)
            g2d.fillOval(getMousePosition().x, getMousePosition().y, 10, 10);

        getBufferStrategy().show();
    }

    public static void main(String[] args)
    {
        TestMain game = new TestMain();
        game.start();
    }
}