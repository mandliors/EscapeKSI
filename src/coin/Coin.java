package coin;

import gameobjects.shapes.TexturedPlain;
import math.Vec3;


public class Coin extends TexturedPlain
{
    private double time;

    public Coin(Vec3 position, Vec3 scale, String textureName)
    {
        super(position, scale, new Vec3(90.0, 0.0, -90.0), textureName);
        time = 0.0;
    }

    public void update(double dt)
    {
        time += dt;

        this.rotate(new Vec3(0.0, 0.0, 200 * dt));
        position = position.add(new Vec3(0.0, Math.sin(time * 3.0) * scale.getX() / 150.0, 0.0));
    }
}
