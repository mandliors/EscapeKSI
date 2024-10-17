import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Game extends JComponent
{
    private final int FPS = 60;
    private final int FRAME_TIME = 1000000000 / FPS;

    private int width, height;
    private BufferedImage framebuffer;

    private Thread gameThread;

    private Camera camera;
    private Renderer renderer;

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

        camera = new Camera();
        renderer = new Renderer(width, height, g2d);

        addTrigons();

        gameThread = new Thread(() -> {
            while (true)
            {
                long startTime = System.nanoTime();
                renderer.render(camera.getViewMatrix());
                render();
                long elapsedTime = System.nanoTime() - startTime;
                if (elapsedTime < FRAME_TIME)
                {
                    try { Thread.sleep((FRAME_TIME - elapsedTime) / 1000000); }
                    catch (Exception e) { }
                }
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

    private void addTrigons()
    {
        Trigon t1 = new Trigon(
                new Vec3(0.5, 0.5, 0.5),
                new Vec3(-0.5, -0.5, 0.5),
                new Vec3(-0.5, 0.5, -0.5),
                Color.CYAN
        );
        Trigon t2 = new Trigon(
                new Vec3(0.5, 0.5, 0.5),
                new Vec3(-0.5, -0.5, 0.5),
                new Vec3(0.5, -0.5, -0.5),
                Color.RED
        );
        Trigon t3 = new Trigon(
                new Vec3(-0.5, 0.5, -0.5),
                new Vec3(0.5, -0.5, -0.5),
                new Vec3(0.5, 0.5, 0.5),
                Color.GREEN
        );
        Trigon t4 = new Trigon(
                new Vec3(-0.5, 0.5, -0.5),
                new Vec3(0.5, -0.5, -0.5),
                new Vec3(-0.5, -0.5, 0.5),
                Color.ORANGE
        );

        renderer.submitTrigon(t1);
        renderer.submitTrigon(t2);
        renderer.submitTrigon(t3);
        renderer.submitTrigon(t4);
    }
}
