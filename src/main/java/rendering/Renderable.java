package main.java.rendering;

/**
 * Renderable interface (renderable objects should implement it)
 */
public interface Renderable
{
    /**
     * Render method that has to be implemented by renderables
     */
    void render(Camera camera);

    /**
     * Returns the rendering priority of the renderable
     */
    int getRenderingPriority();

    /**
     * Sets the rendering priority for the renderable
     */
    void setRenderingPriority(int priority);
}
