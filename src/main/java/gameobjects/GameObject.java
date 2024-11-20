package main.java.gameobjects;

import main.java.meth.Mat4;
import main.java.meth.Meth;
import main.java.meth.Vec3;
import main.java.rendering.Renderable;

public abstract class GameObject implements Renderable
{
    protected Vec3 position = new Vec3(0.0);
    protected Vec3 rotation = new Vec3(0.0);
    protected Vec3 scale = new Vec3(1.0);
    protected int renderingPriority = 1;
    protected boolean collidable = false;
    private Mat4 model = new Mat4(1.0);

    public abstract void update(double dt);

    public Vec3 getPosition() { return position; }
    public Vec3 getRotation() { return rotation; }
    public Vec3 getScale() { return scale; }

    public int getRenderingPriority() { return renderingPriority; }
    public void setRenderingPriority(int priority) { this.renderingPriority = priority; }

    public boolean isCollidable() { return collidable; }
    public void setCollidable(boolean collidable) { this.collidable = collidable; }

    public void translate(Vec3 translation) { this.position = this.position.add(translation); }
    public void rotate(Vec3 rotation) { this.rotation = this.rotation.add(rotation); }
    public void scale(Vec3 scale) { this.scale = this.scale.multiply(scale); }

    public Mat4 getModelMatrix() { _updateModelMatrix(); return model; }
    private void _updateModelMatrix()
    {
        model = new Mat4(1.0);
        model = Meth.translate(model, position);
        model = Meth.scale(model, scale);
        model = Meth.rotateX(model, rotation.getX()); // yaw
        model = Meth.rotateY(model, rotation.getY()); // pitch
        model = Meth.rotateZ(model, rotation.getZ()); // roll
    }
}