package collision;

import gameobjects.GameObject;
import math.Vec3;

public class Collision
{
    private final GameObject collidableGameObject;
    private final GameObject gameObject;
    private final Vec3 displacement;

    private static double colliderPadding = 0.1;

    public GameObject getCollidableGameObject() { return collidableGameObject; }
    public GameObject getGameObject() { return gameObject; }
    public Vec3 getDisplacement() { return displacement; }

    public static void setColliderPadding(double padding) { colliderPadding = padding; }
    public static double getColliderPadding() { return colliderPadding; }

    public Collision(GameObject collidableGameObject, GameObject gameObject)
    {
        this.collidableGameObject = collidableGameObject;
        this.gameObject = gameObject;
        this.displacement = calculateDisplacementFromGameObjects(collidableGameObject, gameObject);
    }

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
