import actors.Player;
import assets.*;
import maze.Maze;
import rendering.*;
import math.*;
import input.*;
import world.World;

import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Game extends Canvas implements Runnable
{
    private static final int WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final int HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private static final int MAX_FPS = 120;
    private static final double FRAME_TIME = 1.0 / MAX_FPS;

    private Player player;
    private double dt = 0.0;

    public Game()
    {
        JFrame frame = new JFrame("Escape KSI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.pack();
        frame.setSize(WIDTH, HEIGHT - 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // double buffering
        this.createBufferStrategy(2);
    }

    public void start()
    {
        Renderer.init(this);
        Renderer.setResolution(0.4);
        Renderer.setBackfaceCulling(true);
        Input.init(this);
        AssetManager.init();

//        Maze maze = new Maze(Maze.defaultMaze);
//        maze.saveToFile("res/dick.maze");
//        maze.build();

        Maze maze = Maze.loadFromFile("res/dick.maze");
        maze.build();

        player = new Player(new Vec3(0.0, 2.01, 0.0), new Vec3(2.0, 0.0, 2.0), new Vec3(0.0, 0.0, -1.0), 45.0);
        player.setCollidable(true);
        World.addGameObject(player);

        new Thread(this).start();
    }

    @Override
    public void run()
    {
        Clip c = AssetManager.loadSound("music", "res/thick_of_it.wav");
        c.setMicrosecondPosition((long)(57.3 * 1_000_000));
        // c.start();

        while (true)
        {
            long startTime = System.nanoTime();


            if (Input.isKeyPressed(KeyEvent.VK_E))
                c.start();
            if (Input.isKeyPressed(KeyEvent.VK_R))
                c.stop();
            if (Input.isKeyPressed(KeyEvent.VK_UP))
                Renderer.setResolution(Renderer.getResolution() / 0.8);
            else if (Input.isKeyPressed(KeyEvent.VK_DOWN))
                Renderer.setResolution(Renderer.getResolution() * 0.8);
            if (Input.isKeyPressed(KeyEvent.VK_ESCAPE))
                System.exit(0);

            World.update(dt);
            World.render(this, player.getCamera());


            dt = (System.nanoTime() - startTime) / 1_000_000_000.0;
            if (dt < FRAME_TIME)
            {
                try { Thread.sleep((long)(1000 * (FRAME_TIME - dt))); } catch (Exception e) { e.printStackTrace(); }
                dt = FRAME_TIME;
            }
        }
    }

    public static void main(String[] args)
    {
        Game game = new Game();
        game.start();
    }
}