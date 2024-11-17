package world;

import assets.AssetManager;
import collision.Collision;
import gameobjects.GameObject;
import gameobjects.shapes.ColoredGameObject;
import input.Input;
import math.Vec3;
import rendering.Camera;
import rendering.Renderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class World
{
    private static final List<GameObject> gameObjects = new ArrayList<>();

    private static final Font bigFont = new Font("Monospaced", Font.BOLD, 40);
    private static double dt = 0.0;

    public static void addGameObject(GameObject obj) { gameObjects.add(obj); }
    public static void removeGameObject(GameObject obj) { gameObjects.remove(obj); }
    public static void clearGameObjects() { gameObjects.clear(); }

    public static void update(double dt)
    {
        World.dt = dt;

        // update the gameobjects
        gameObjects.forEach((GameObject go) -> go.update(dt));

        // check for collisions
        HashMap<GameObject, Collision> collisions = new HashMap<>();
        for (GameObject go1 : gameObjects)
        {
            if (!go1.isCollidable()) continue;

            for (GameObject go2 : gameObjects)
            {
                if (go2.isCollidable()) continue;

                if (Collision.collideWith(go1, go2))
                {
                    Collision newCollision = new Collision(go1, go2);
                    Collision prevCollision = collisions.get(go1);
                    if (prevCollision == null)
                        collisions.put(go1, newCollision);
                    else
                    {
                        double distSqrPrev = go1.getPosition().subtract(prevCollision.getGameObject().getPosition()).getLengthSquared();
                        double distSqrNew = go1.getPosition().subtract(newCollision.getGameObject().getPosition()).getLengthSquared();
                        collisions.put(go1, distSqrPrev < distSqrNew ? prevCollision : newCollision);
                    }
                }
            }
        }

        // resolve collisions
        for (GameObject go : gameObjects)
        {
            Collision collision = collisions.get(go);
            if (collision != null)
                go.translate(collision.getDisplacement());
        }

        Input.update();
        AssetManager.update(dt);
    }

    public static void render(Canvas canvas, Camera camera)
    {
        Graphics2D g2d = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();

        Renderer.render(g2d, camera, gameObjects);

        g2d.setColor(Color.yellow);
        Point mousePos = canvas.getMousePosition();
        if (mousePos != null)
            g2d.fillOval(mousePos.x - 5, mousePos.y - 5, 10, 10);

        g2d.setColor(Color.GREEN);
        g2d.setFont(bigFont);
        g2d.drawString(String.format("FPS: %d", (int)(1.0 / dt)), 10, 50);
        g2d.drawString(String.format("Res: %d%%", (int)(100 * Renderer.getResolution())), 10, 100);

        g2d.dispose();
        canvas.getBufferStrategy().show();
    }
}
