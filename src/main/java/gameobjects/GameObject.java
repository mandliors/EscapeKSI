package main.java.gameobjects;

import main.java.meth.Mat4;
import main.java.meth.Meth;
import main.java.meth.Vec3;
import main.java.rendering.Renderable;

public abstract class GameObject implements Renderable
{
    /**
     * The position of the gameobject
     */
    protected Vec3 position = new Vec3(0.0);
    /**
     * The rotation of the gameobject
     */
    protected Vec3 rotation = new Vec3(0.0);
    /**
     * The scale of the gameobject
     */
    protected Vec3 scale = new Vec3(1.0);
    /**
     * The lower the renderingPriority, the earlier it will be rendered
      */
    protected int renderingPriority = 1;
    /**
     * Whether the object is a collidable gameobject
     */
    protected boolean collidable = false;
    /**
     * Model matrix that stores all the transformations
     */
    private Mat4 model = new Mat4(1.0);

    /**
     * The update method (must be implemented in derived classes)
     */
    public abstract void update(double dt);

    /**
     * Returns the position of the gameobject
     */
    public Vec3 getPosition() { return position; }
    /**
     * Returns the rotation of the gameobject
     */
    public Vec3 getRotation() { return rotation; }
    /**
     * Returns the scale of the gameobject
     */
    public Vec3 getScale() { return scale; }

    /**
     * Returns the rendering priority of the gameobject
     */
    public int getRenderingPriority() { return renderingPriority; }
    /**
     * Sets the rendering priority of the gameobject
     */
    public void setRenderingPriority(int priority) { this.renderingPriority = priority; }

    /**
     * Returns if the gameobject is collidable
     */
    public boolean isCollidable() { return collidable; }
    /**
     * Sets if the gameobject's collidable property
     */
    public void setCollidable(boolean collidable) { this.collidable = collidable; }

    /**
     * Translates the gameobject by the given parameter
     */
    public void translate(Vec3 translation) { this.position = this.position.add(translation); }
    /**
     * Rotates the gameobject by the given parameter
     */
    public void rotate(Vec3 rotation) { this.rotation = this.rotation.add(rotation); }
    /**
     * Scales the gameobject by the given parameter
     */
    public void scale(Vec3 scale) { this.scale = this.scale.multiply(scale); }

    /**
     * Recalculates the model matrix and returns it
     */
    public Mat4 getModelMatrix() { _updateModelMatrix(); return model; }

    /**
     * Calculates the model matrix by the gameobject's position, scale and the rotation
     */
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
