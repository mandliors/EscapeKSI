package main.java.collision;

import main.java.gameobjects.GameObject;
import main.java.meth.Vec3;

public class Collision
{
    /**
     * The collidable gameobject of the collision
     */
    private final GameObject collidableGameObject;
    /**
     * The non-collidable gameobject of the collision
     */
    private final GameObject gameObject;
    /**
     * The displacement of the collision
     */
    private final Vec3 displacement;

    /**
     * The padding that every gameobject's collider has
     */
    private static double colliderPadding = 0.1;

    /**
     * Returns the collidable gameobject of the collision
     */
    public GameObject getCollidableGameObject() { return collidableGameObject; }
    /**
     * Returns the non-collidable gameobject of the collision
     */
    public GameObject getGameObject() { return gameObject; }
    /**
     * Returns the displacement of the collision
     */
    public Vec3 getDisplacement() { return displacement; }

    /**
     * Sets the collider padding for the gameobjects
     */
    public static void setColliderPadding(double padding) { colliderPadding = padding; }

    /**
     * Returns the collider padding for the gameobjects
     */
    public static double getColliderPadding() { return colliderPadding; }

    /**
     * Creates a collision between a collidable gameobject and a non-collidable gameobject
     * Also calculates the displacement, which the collidable gameobject should be translated with
     */
    public Collision(GameObject collidableGameObject, GameObject gameObject)
    {
        this.collidableGameObject = collidableGameObject;
        this.gameObject = gameObject;
        this.displacement = calculateDisplacementFromGameObjects(collidableGameObject, gameObject);
    }

    /**
     * Checks an AABB collision between the gameobjects and returns if they collide
     * Does not take the rotations into consideration
     */
    public static boolean collideWith(GameObject go1, GameObject go2)
    {
        Vec3 go1Scale = go1.getScale().add(new Vec3(colliderPadding * 2.0));
        Vec3 go2Scale = go2.getScale().add(new Vec3(colliderPadding * 2.0));

        if (go1.getPosition().getX() + go1Scale.getX() * 0.5 < go2.getPosition().getX() - go2Scale.getX() * 0.5 ||
                go2.getPosition().getX() + go2Scale.getX() * 0.5 < go1.getPosition().getX() - go1Scale.getX() * 0.5) return false;

        if (go1.getPosition().getY() + go1Scale.getY() * 0.5 < go2.getPosition().getY() - go2Scale.getY() * 0.5 ||
                go2.getPosition().getY() + go2Scale.getY() * 0.5 < go1.getPosition().getY() - go1Scale.getY() * 0.5) return false;

        if (go1.getPosition().getZ() + go1Scale.getZ() * 0.5 < go2.getPosition().getZ() - go2Scale.getZ() * 0.5 ||
                go2.getPosition().getZ() + go2Scale.getZ() * 0.5 < go1.getPosition().getZ() - go1Scale.getZ() * 0.5) return false;

        return true;
    }

    /**
     * Calculates the displacement along all three axis and only keeps the smallest displacement
     * @param cgo The collidable gameobject
     * @param go The non-collidable gameobject
     * @return Returns a vec3 with only one non-zero component (the smallest displacement which should be the translation)
     */
    private static Vec3 calculateDisplacementFromGameObjects(GameObject cgo, GameObject go)
    {
        Vec3 centerToCenter = cgo.getPosition().subtract(go.getPosition());
        Vec3 absoluteDisplacement = new Vec3(
                 0.5 * (cgo.getScale().getX() + go.getScale().getX() + 4 * colliderPadding) - Math.abs(centerToCenter.getX()),
                 0.5 * (cgo.getScale().getY() + go.getScale().getY() + 4 * colliderPadding) - Math.abs(centerToCenter.getY()),
                 0.5 * (cgo.getScale().getZ() + go.getScale().getZ() + 4 * colliderPadding) - Math.abs(centerToCenter.getZ())
        );
        Vec3 displacement = new Vec3(
                Math.signum(centerToCenter.getX()) * absoluteDisplacement.getX(),
                Math.signum(centerToCenter.getY()) * absoluteDisplacement.getY(),
                Math.signum(centerToCenter.getZ()) * absoluteDisplacement.getZ()
        );

        if (absoluteDisplacement.getX() < absoluteDisplacement.getY())
        {
            if (absoluteDisplacement.getZ() < absoluteDisplacement.getX())
                return new Vec3(0.0, 0.0, displacement.getZ());
            else
                return new Vec3(displacement.getX(), 0.0, 0.0);
        }
        else
        {
            if (absoluteDisplacement.getZ() < absoluteDisplacement.getY())
                return new Vec3(0.0, 0.0, displacement.getZ());
            else
                return new Vec3(0.0, displacement.getY(), 0.0);
        }
    }
}
