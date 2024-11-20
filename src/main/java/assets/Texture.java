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
    private List<BufferedImage> frames = new ArrayList<>();
    private final double frameDuration;
    private double currentTime;

    public Texture() { this("placeholder"); /* this should not exist, so it will be a magenta colored pixel */ }
    public Texture(String path) { this(0.0, path); }
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

    public void setTime(double animationTime) { currentTime = animationTime; }
    public void reset() { currentTime = 0.0; }

    public BufferedImage get() { return frames.get((int)(currentTime / frameDuration) % frames.size()); }
}
