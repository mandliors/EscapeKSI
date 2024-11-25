package main.java.screens;

import main.java.assets.AssetManager;
import main.java.input.Input;
import main.java.maze.Maze;
import main.java.rendering.Renderer;
import main.java.rendering.RendererString;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class GameScreen extends JPanel
{
    /**
     * The selected maze name to be loaded
     */
    public static String selectedMaze = "Backrooms_KSI";
    /**
     * Field of view of the player's camera
     */
    public static double FOV = 45;
    /**
     * Mouse sensitivity for the camera
     */
    public static double mouseSensitivity = 0.06;

    /**
     * Maximum FPS
     */
    private static final int MAX_FPS = 120;
    /**
     * Frame time corresponding the max fps
     */
    private static final double FRAME_TIME = 1.0 / MAX_FPS;

    /**
     * The loaded maze
     */
    private Maze maze;
    /**
     * Delta time between frames, needed for smooth movement
     */
    private double dt = 0.0;
    /**
     * The double-buffered canvas used for rendering
     */
    private Canvas canvas;
    /**
     * Whether the game is active
     */
    private boolean playing;

    /**
     * Creates a new game panel on the parent frame with the given dimensions
     */
    public GameScreen(JFrame parent, int width, int height)
    {
        setLayout(null);
        setSize(width, height);
        setFocusable(false);
        parent.setContentPane(this);

        // canvas to render to
        canvas = new Canvas();
        canvas.setSize(width, height);
        canvas.setFocusable(false);
        add(canvas);

        // double buffering
        canvas.createBufferStrategy(2);

        // init everything
        Renderer.init(canvas);
        Renderer.setResolution(0.4);
        Renderer.setBackfaceCulling(true);
        Input.init(parent);
        AssetManager.init();

        // load everything

        //          BACKROOMS_KSI
//        maze = new Maze(Maze.defaultMaze,
//                new Color(191, 172, 44),
//                new Color(77, 67, 23),
//                new Color(77, 67, 23),
//                new String[] {
//                        "res/mazes/Backrooms_KSI/ksi1.png",
//                        "res/mazes/Backrooms_KSI/ksi2.png"
//                },
//                0.4,
//                "res/mazes/Backrooms_KSI/prime.png",
//                "res/mazes/Backrooms_KSI/thick_of_it.wav",
//                "res/mazes/Backrooms_KSI/burp.wav",
//                "res/mazes/Backrooms_KSI/get_out.wav"
//        );

//        SKIBIDI TOILET
//        maze = new Maze(Maze.defaultMaze,
//                new Color(44, 110, 191),
//                new Color(22, 68, 80),
//                new Color(22, 68, 80),
//                new String[] {
//                    "res/mazes/Skibidi_Toilet/skibidi1.png",
//                    "res/mazes/Skibidi_Toilet/skibidi2.png",
//                    "res/mazes/Skibidi_Toilet/skibidi3.png",
//                    "res/mazes/Skibidi_Toilet/skibidi4.png",
//                    "res/mazes/Skibidi_Toilet/skibidi5.png",
//                    "res/mazes/Skibidi_Toilet/skibidi6.png",
//                    "res/mazes/Skibidi_Toilet/skibidi7.png",
//                    "res/mazes/Skibidi_Toilet/skibidi8.png"
//                },
//                0.2,
//                "res/mazes/Skibidi_Toilet/toilet.png",
//                "res/mazes/Skibidi_Toilet/skibidi_dap_dap_dap_yes_yes.wav", "res/mazes/Skibidi_Toilet/toilet_flush.wav",
//                "res/mazes/Skibidi_Toilet/get_out.wav"
//        );



        //maze.saveToFile("res/mazes/" + selectedMaze + "/" + selectedMaze + ".maze");
        maze = Maze.loadFromFile("res/mazes/" + selectedMaze + "/" + selectedMaze + ".maze");

        playing = true;
    }

    /**
     * Starts the game loop: updates and renders everything
     */
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

    /**
     * The update functionality of the game
     */
    private void update(double dt)
    {
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

    /**
     * The render functionality of the game
     */
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