package collision;

import gameobjects.GameObject;
import math.Vec3;

public class Collision
{
    private final GameObject collidableGameObject;
    private final GameObject gameObject;
    private final Vec3 displacement;

    public GameObject getCollidableGameObject() { return collidableGameObject; }
    public GameObject getGameObject() { return gameObject; }
    public Vec3 getDisplacement() { return displacement; }

    public Collision(GameObject collidableGameObject, GameObject gameObject)
    {
        this.collidableGameObject = collidableGameObject;
        this.gameObject = gameObject;
        this.displacement = calculateDisplacementFromGameObjects(collidableGameObject, gameObject);
    }

    public static boolean collideWith(GameObject go1, GameObject go2)
    {
        if (go1.getPosition().getX() + go1.getScale().getX() * 0.5 < go2.getPosition().getX() - go2.getScale().getX() * 0.5 ||
                go2.getPosition().getX() + go2.getScale().getX() * 0.5 < go1.getPosition().getX() - go1.getScale().getX() * 0.5) return false;

        if (go1.getPosition().getY() + go1.getScale().getY() * 0.5 < go2.getPosition().getY() - go2.getScale().getY() * 0.5 ||
                go2.getPosition().getY() + go2.getScale().getY() * 0.5 < go1.getPosition().getY() - go1.getScale().getY() * 0.5) return false;

        if (go1.getPosition().getZ() + go1.getScale().getZ() * 0.5 < go2.getPosition().getZ() - go2.getScale().getZ() * 0.5 ||
                go2.getPosition().getZ() + go2.getScale().getZ() * 0.5 < go1.getPosition().getZ() - go1.getScale().getZ() * 0.5) return false;

        return true;
    }

    private static Vec3 calculateDisplacementFromGameObjects(GameObject cgo, GameObject go)
    {
        Vec3 centerToCenter = cgo.getPosition().subtract(go.getPosition());
        Vec3 absoluteDisplacement = new Vec3(
                 0.5 * (cgo.getScale().getX() + go.getScale().getX()) - Math.abs(centerToCenter.getX()),
                 0.5 * (cgo.getScale().getY() + go.getScale().getY()) - Math.abs(centerToCenter.getY()),
                 0.5 * (cgo.getScale().getZ() + go.getScale().getZ()) - Math.abs(centerToCenter.getZ())
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
