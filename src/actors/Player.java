package actors;

import gameobjects.shapes.ColoredCube;
import math.Vec3;
import rendering.Camera;

import java.awt.*;

public class Player extends ColoredCube
{
    private final Camera camera;

    public Player(Vec3 position, Vec3 scale, Vec3 lookDirection, double fov)
    {
        super(position, scale, new Vec3(0.0), new Color(0));

        camera = new Camera(fov, 0.01, 200.0);
        camera.setPosition(position);
        camera.lookAt(position.add(lookDirection));
        camera.setMouseLock(true);
    }

    public Camera getCamera() { return camera; }

    public void update(double dt)
    {
        // Vec3 offset = camera.getFront().multiply(new Vec3(1.0, 0.0, 1.0)).normalize().scale(10.0).add(new Vec3(0.0, -5.0, 0.0));
        Vec3 offset = new Vec3(0.0);
        camera.setPosition(position.subtract(offset));
        camera.update(dt);
        position = camera.getPosition().add(offset);
    }
}
