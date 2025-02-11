package main.java.assets;

import com.sun.source.doctree.EscapeTree;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;

public class AssetManager
{
    /**
     * The collection of the loaded textures (texture name, texture object)
     */
    private static final HashMap<String, Texture> textures = new HashMap<>();
    /**
     * The collection of the loaded sounds (sound name, clip object)
     */
    private static final HashMap<String, Clip> sounds = new HashMap<>();
    /**
     * The global animation time, used for updating the loaded animated textures
     */
    private static double animationTime;

    /**
     * Initializes the AssetManager, creates a placeholder texture
     */
    public static void init()
    {
        animationTime = 0.0;
        textures.put("placeholder", new Texture());

        try
        {
            float sampleRate = 44100;

            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            byte[] silence = new byte[(int) sampleRate * 2]; // 2 bytes per sample for 16-bit audio

            // create a data stream from the silence array
            AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(silence), format, (int) sampleRate);

            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioInputStream);

            sounds.put("placeholder", clip);
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Updates the time to make animations update correctly
     */
    public static void update(double dt)
    {
        animationTime += dt;
        textures.values().forEach((var t) -> t.setTime(animationTime));
    }

    /**
     * Resets each texture
     */
    public static void reset() { textures.values().forEach(Texture::reset); }

    /**
     * Loads a texture (with no animation)
     */
    public static Texture loadTexture(String name, String path) { return loadTexture(name, 0.0, path); }
    /**
     * Loads a texture with animation
     * @param name Name of the texture
     * @param frameDuration How long each frame takes in the animation in seconds
     * @param pathList An array of paths for the frames of the animation
     */
    public static Texture loadTexture(String name, double frameDuration, String... pathList)
    {
        Texture texture = new Texture(frameDuration, pathList);
        textures.put(name, texture);
        return texture;
    }

    /**
     * Loads a sound and gives it a name
     */
    public static Clip loadSound(String name, String path)
    {
        try
        {
            Clip clip = AudioSystem.getClip(AudioSystem.getMixer(null).getMixerInfo());
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

    /**
     * Retrieves a texture by its name
     * @return Returns the texture if it exists, or the placeholder texture if not
     */
    public static Texture getTexture(String name)
    {
        Texture t = textures.get(name);
        if (t != null) return t;
        else return textures.get("placeholder");
    }

    /**
     * Returns the sound by its name
     */
    public static Clip getSound(String name)
    {
        Clip c = sounds.get(name);
        if (c != null) return c;
        else return sounds.get("placeholder");
    }
}
