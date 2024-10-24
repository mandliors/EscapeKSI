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
    private final int FRAME_TIME = 1000000000 / FPS;
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

        Input input = new Input();
        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                input.setKeyDown(e.getKeyCode(), true);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                input.setKeyDown(e.getKeyCode(), false);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                input.setMouseButtonDown(e.getButton(), true);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                input.setMouseButtonDown(e.getButton(), false);
            }
        });
        new Thread(() -> {
            while (true)
            {
                short dx = 0, dz = 0;
                if (input.isKeyDown(KeyEvent.VK_W)) dz += -1;
                if (input.isKeyDown(KeyEvent.VK_A)) dx += -1;
                if (input.isKeyDown(KeyEvent.VK_S)) dz += 1;
                if (input.isKeyDown(KeyEvent.VK_D)) dx += 1;
                camera.translate(new Vec3(dx * CAMERA_SPEED, 0.0, dz * CAMERA_SPEED));
                sleepNoThrow(5);
            }
        }).start();

        Tetrahedron tetra = new Tetrahedron();
        Cube cube = new Cube();

        Thread gameThread = new Thread(() -> {
            while (true)
            {
                long startTime = System.nanoTime();

                tetra._rotate();
                cube._rotate();
                renderer.add(cube);
                renderer.render(camera.getProjection(), camera.getView());
                render();

                long elapsedTime = System.nanoTime() - startTime;
                if (elapsedTime < FRAME_TIME)
                    sleepNoThrow((FRAME_TIME - elapsedTime) / 1000000);
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
