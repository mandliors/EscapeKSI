package main.java.rendering;

import java.awt.*;

public class RendererString
{
    public String string;
    public Color color;
    public boolean left;

    public RendererString(String string, Color color, boolean left)
    {
        this.string = string;
        this.color = color;
        this.left = left;
    }
}
