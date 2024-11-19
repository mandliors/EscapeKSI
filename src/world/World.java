package world;

import assets.AssetManager;
import collision.Collision;
import gameobjects.GameObject;
import gameobjects.shapes.ColoredGameObject;
import input.Input;
import rendering.Camera;
import rendering.Renderer;
import rendering.RendererString;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class World
{
    private static final List<GameObject> gameObjects = new ArrayList<>();

    private static double dt = 0.0;

    public static void addGameObject(GameObject obj) { gameObjects.add(obj); }
    public static void removeGameObject(GameObject obj) { gameObjects.remove(obj); }
    public static void clearGameObjects() { gameObjects.clear(); }

    public static void update(double dt)
    {
        World.dt = dt;

        gameObjects.forEach((GameObject go) -> go.update(dt));

        Input.update();
        AssetManager.update(dt);
    }

    public static void render(Canvas canvas, Camera camera)
    {
        Graphics2D g2d = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();

        Renderer.addString(new RendererString(String.format("FPS: %d", (int)(1.0 / dt)), Color.GREEN));
        Renderer.addString(new RendererString(String.format("Res: %d%%", (int)(100 * Renderer.getResolution())), Color.GREEN));

        Renderer.render(g2d, camera, gameObjects);

        g2d.dispose();
        canvas.getBufferStrategy().show();
    }
}
