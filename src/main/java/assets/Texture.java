package main.java.assets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Texture
{
    /**
     * The list of the frames in the animation
     */
    private List<BufferedImage> frames = new ArrayList<>();
    /**
     * The duration of each frame in seconds
     */
    private final double frameDuration;
    /**
     * Current time of the animation, needed for correct frame switching
     */
    private double currentTime;

    /**
     * Invalid texture (placeholder named texture should not exist), so it will be a magenta pixel
     */
    public Texture() { this("placeholder"); /* this should not exist, so it will be a magenta colored pixel */ }

    /**
     * Creates a texture (no animation here)
     */
    public Texture(String path) { this(0.0, path); }

    /**
     * Creates an animated texture
     * @param frameDuration How long each frame takes (in seconds)
     * @param pathList Path list of the frames
     */
    public Texture(double frameDuration, String... pathList)
    {
        this.frameDuration = frameDuration;
        currentTime = 0.0;
        for (String path : pathList)
        {
            try { frames.add(ImageIO.read(new File(path))); }
            catch (IOException e)
            {
                BufferedImage placeholder = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
                placeholder.setRGB(0, 0, Color.MAGENTA.getRGB());
                frames.add(placeholder);
            }
        }
    }

    /**
     * Sets the time of the animation
     */
    public void setTime(double animationTime) { currentTime = animationTime; }

    /**
     * Resets the time of the animation to 0
     */
    public void reset() { currentTime = 0.0; }

    /**
     * Returns the current frame in the animated texture or the texture if no animation is available
     */
    public BufferedImage get() { return frames.get((int)(currentTime / frameDuration) % frames.size()); }
}
