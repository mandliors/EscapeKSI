package main.java.coin;

import main.java.gameobjects.shapes.TexturedPlain;
import main.java.meth.Vec3;


public class Coin extends TexturedPlain
{
    /**
     * Time needed for sinusoid movement
     */
    private double time;

    /**
     * Constructs a coin with the given position, scale and the name of the texture
     */
    public Coin(Vec3 position, Vec3 scale, String textureName)
    {
        super(position, scale, new Vec3(90.0, 0.0, -90.0), textureName);
        time = 0.0;
    }

    /**
     * Updates the coin, so basically moves it up and down and spins it
     */
    public void update(double dt)
    {
        time += dt;

        this.rotate(new Vec3(0.0, 0.0, 200 * dt));
        position = position.add(new Vec3(0.0, Math.sin(time * 3.0) * scale.getX() / 150.0, 0.0));
    }
}
