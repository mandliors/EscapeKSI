package screens;

import assets.*;
import maze.Maze;
import input.*;
import rendering.Renderer;
import rendering.RendererString;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class GameScreen extends JPanel
{
    private static final int MAX_FPS = 120;
    private static final double FRAME_TIME = 1.0 / MAX_FPS;

    private Maze maze;
    private double dt = 0.0;
    private Canvas canvas;
    private boolean playing;

    private Clip music;

    public GameScreen(JFrame parent, int width, int height)
    {
        setLayout(null);
        setSize(width, height);
        parent.setContentPane(this);

        // canvas to render to
        canvas = new Canvas();
        canvas.setSize(width, height);
        add(canvas);

        // double buffering
        canvas.createBufferStrategy(2);

        // init everything
        Renderer.init(canvas);
        Renderer.setResolution(0.4);
        Renderer.setBackfaceCulling(true);
        Input.init(this);
        AssetManager.init();

        // load everything
        maze = new Maze(Maze.defaultMaze, "res/prime.png", "res/ksi.png");
        maze.saveToFile("res/backrooms.maze");
//        maze = Maze.loadFromFile("res/backrooms.maze");
        music = AssetManager.loadSound("music", "res/thick_of_it.wav");

        playing = true;
    }

    public void display()
    {
        while (playing)
        {
            long startTime = System.nanoTime();

            update(dt);
            render();

            dt = (System.nanoTime() - startTime) / 1_000_000_000.0;
            if (dt < FRAME_TIME)
            {
                try { Thread.sleep((long)(1000 * (FRAME_TIME - dt))); } catch (Exception e) { e.printStackTrace(); }
                dt = FRAME_TIME;
            }
        }
    }

    private void update(double dt)
    {
        if (Input.isKeyPressed(KeyEvent.VK_E))
        {
            music.setMicrosecondPosition((long)(57.3 * 1_000_000));
            music.start();
        }
        if (Input.isKeyPressed(KeyEvent.VK_R))
            music.stop();
        if (Input.isKeyPressed(KeyEvent.VK_UP))
            Renderer.setResolution(Renderer.getResolution() / 0.8);
        else if (Input.isKeyPressed(KeyEvent.VK_DOWN))
            Renderer.setResolution(Renderer.getResolution() * 0.8);
        if (Input.isKeyPressed(KeyEvent.VK_SPACE))
            Renderer.setWireframe(!Renderer.isWireframeEnabled());

        if (!maze.update(dt))
            playing = false;

        Input.update();
        AssetManager.update(dt);
    }

    private void render()
    {
        Graphics2D g2d = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();

        Renderer.addString(new RendererString(String.format("FPS: %d", (int)(1.0 / dt)), Color.GREEN, true));
        Renderer.addString(new RendererString(String.format("Res: %d%%", (int)(100 * Renderer.getResolution())), Color.GREEN, true));

        maze.render();
        Renderer.render(g2d, maze.getPlayer().getCamera());

        g2d.dispose();
        canvas.getBufferStrategy().show();
    }
}