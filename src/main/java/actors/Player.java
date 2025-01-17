package main.java.actors;

import main.java.gameobjects.shapes.ColoredCube;
import main.java.meth.Vec3;
import main.java.rendering.Camera;
import main.java.screens.GameScreen;

import java.awt.*;

public class Player extends ColoredCube
{
    private final Camera camera;

    /**
     * Constructs a player with the given position, scale, look direction and field of view
     */
    public Player(Vec3 position, Vec3 scale, Vec3 lookDirection, double fov)
    {
        super(position, scale, new Vec3(0.0), new Color(0));

        camera = new Camera(fov, GameScreen.mouseSensitivity, 0.01, 50.0);
        camera.setPosition(position);
        camera.lookAt(position.add(lookDirection));
        camera.setMouseLock(true);
    }

    /**
     * Returns the camera of the player
     */
    public Camera getCamera() { return camera; }

    /**
     * Updates the player (moves the camera in the correct direction and follows it
     */
    public void update(double dt)
    {
        // Vec3 offset = camera.getFront().multiply(new Vec3(1.0, 0.0, 1.0)).normalize().scale(10.0).add(new Vec3(0.0, -5.0, 0.0));
        Vec3 offset = new Vec3(0.0);
        camera.setPosition(position.subtract(offset));
        camera.update(dt);
        position = camera.getPosition().add(offset);
    }
}
