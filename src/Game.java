import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Game extends JComponent
{
    private final int FPS = 60;
    private final int FRAME_TIME = 1000 / FPS;
    private final double CAMERA_SPEED = 0.03;

    private int width, height;
    private BufferedImage framebuffer;

    private Camera camera;
    private Renderer renderer;
    private Input input;

    public void init()
    {
        width = getWidth();
        height = getHeight();

        framebuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = framebuffer.createGraphics();
        g2d.setRenderingHints(new HashMap<RenderingHints.Key, Object>() { {
            put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        } });

        camera = new Camera(45.0, (double)width / height, 0.1, 100.0);
        renderer = new Renderer(width, height, g2d);

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

        Tetrahedron tetra = new Tetrahedron();
        Cube cube = new Cube();

        Thread gameThread = new Thread(() -> {
            double dt = 0.0;
            while (true)
            {
                long startTime = System.currentTimeMillis();

                // update
                short dx = 0, dz = 0;
                if (input.isKeyDown(KeyEvent.VK_W)) dz += -1;
                if (input.isKeyDown(KeyEvent.VK_A)) dx += -1;
                if (input.isKeyDown(KeyEvent.VK_S)) dz += 1;
                if (input.isKeyDown(KeyEvent.VK_D)) dx += 1;
                camera.translate(new Vec2(dx * dt, dz * dt));
                //camera.rotate(Input.getMouseDelta().scale(dt * 0.01));

                tetra._rotate();
                cube._rotate();

                // render
                renderer.add(cube);
                renderer.add(tetra);
                renderer.render(camera.getProjection(), camera.getView());
                render();

                long elapsedTime = System.currentTimeMillis() - startTime;
                dt = elapsedTime / 1000.0;
                if (elapsedTime < FRAME_TIME)
                    sleepNoThrow((FRAME_TIME - elapsedTime) / 1000);
            }
        });
        gameThread.start();
    }

    private void render()
    {
        Graphics g = getGraphics();
        g.drawImage(framebuffer, 0, 0, null);
        g.dispose();
    }

    private void sleepNoThrow(long time)
    {
        try { Thread.sleep(time); }
        catch (Exception e) { }
    }
}
