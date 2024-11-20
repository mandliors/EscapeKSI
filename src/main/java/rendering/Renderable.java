package main.java.rendering;


public interface Renderable
{
    void render(Camera camera);

    int getRenderingPriority();
    void setRenderingPriority(int priority);
}
