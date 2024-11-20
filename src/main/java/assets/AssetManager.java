package main.java.assets;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.HashMap;

public class AssetManager
{
    private static final HashMap<String, Texture> textures = new HashMap<>();
    private static final HashMap<String, Clip> sounds = new HashMap<>();
    private static double animationTime;

    public static void init()
    {
        animationTime = 0.0;
        textures.put("placeholder", new Texture());
    }
    public static void update(double dt)
    {
        animationTime += dt;
        textures.values().forEach((var t) -> t.setTime(animationTime));
    }
    public static void reset() { textures.values().forEach(Texture::reset); }

    public static Texture loadTexture(String name, String path) { return loadTexture(name, 0.0, path); }
    public static Texture loadTexture(String name, double frameDuration, String... pathList)
    {
        Texture texture = new Texture(frameDuration, pathList);
        textures.put(name, texture);
        return texture;
    }

    public static Clip loadSound(String name, String path)
    {
        try
        {
            Clip clip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
            clip.open(ais);
            sounds.put(name, clip);
            return clip;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Texture getTexture(String name)
    {
        Texture t = textures.get(name);
        if (t != null) return t;
        else return textures.get("placeholder");
    }
    public static Clip getSound(String name) { return sounds.get(name); }
}
