package main.java.rendering;

import java.awt.*;

/**
 * String class used for easy text rendering
 */
public class RendererString
{
    /**
     * The actual string
     */
    public String string;
    /**
     * The color of the string
     */
    public Color color;
    /**
     * Whether it should be rendered in the top left or top right corner
     */
    public boolean left;

    /**
     * Creates a renderer string object with the given string, color and position
     */
    public RendererString(String string, Color color, boolean left)
    {
        this.string = string;
        this.color = color;
        this.left = left;
    }
}
