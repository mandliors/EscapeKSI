package main.java.actors;

import main.java.gameobjects.shapes.TexturedPlain;
import main.java.meth.Vec3;


public class Enemy extends TexturedPlain
{
    /**
     * The directions in which the enemy can move
     */
    public enum MoveDirection { NONE, UP, RIGHT, DOWN, LEFT }

    /**
     * Current move direction
     */
    private MoveDirection moveDirection = MoveDirection.NONE;
    /**
     * The move speed of the player
     */
    private final double moveSpeed;

    /**
     * Constructs an enemy with the given position, scale, texture name and move speed
     */
    public Enemy(Vec3 position, Vec3 scale, String textureName, double moveSpeed)
    {
        super(position, scale, new Vec3(90.0, 0.0, 0.0), textureName);
        this.moveSpeed = moveSpeed;
    }

    /**
     * Sets the move direction and rotates accordingly so that the enemy is moved in the right direction and looks correct
     */
    public void setMoveDirection(MoveDirection moveDirection)
    {
        this.moveDirection = moveDirection;
        switch (moveDirection)
        {
            case MoveDirection.RIGHT:
            case MoveDirection.LEFT:
                rotation = new Vec3(90.0, 0.0, 90.0);
                break;
            case MoveDirection.UP:
            case MoveDirection.DOWN:
                rotation = new Vec3(90.0, 0.0, 0.0);
                break;
        }
    }

    /**
     * Returns in which direction the enemy is moving
     */
    public MoveDirection getMoveDirection() { return moveDirection; }

    /**
     * Moves the player in the previously set direction
     */
    public void update(double dt)
    {
        switch (moveDirection)
        {
            case MoveDirection.UP -> translate(new Vec3(0.0, 0.0, -moveSpeed * dt));
            case MoveDirection.RIGHT -> translate(new Vec3(moveSpeed * dt, 0.0, 0.0));
            case MoveDirection.DOWN -> translate(new Vec3(0.0, 0.0, moveSpeed * dt));
            case MoveDirection.LEFT -> translate(new Vec3(-moveSpeed * dt, 0.0, 0.0));
            case MoveDirection.NONE -> {}
        }
    }
}
