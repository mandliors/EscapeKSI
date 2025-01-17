package main.java.rendering;

import java.awt.*;
import java.awt.event.KeyEvent;

import main.java.input.Input;
import main.java.meth.Mat4;
import main.java.meth.Meth;
import main.java.meth.Vec2;
import main.java.meth.Vec3;

public class Camera
{
    /**
     * The position of the camera
     */
    private Vec3 position;
    /**
     * The up direction of the world
     */
    private Vec3 worldUp;
    /**
     * The front direction of the camera
     */
    private Vec3 front;
    /**
     * The rifht direction of the camera
     */
    private Vec3 right;
    /**
     * The up direction of the camera
     */
    private Vec3 up;
    /**
     * The yaw rotation of the camera
     */
    private double yaw;
    /**
     * The pitch rotation of the camera
     */
    private double pitch;
    /**
     * The move speed of the camera
     */
    private double moveSpeed;
    /**
     * The mouse sensitivity of the camera
     */
    private double mouseSensitivity;

    /**
     * The projection matrix of the camera
     */
    private Mat4 projection;
    /**
     * The view matrix of the camera
     */
    private Mat4 view;

    /**
     * The center of the screen (needed for mouse locking)
     */
    private static Point centerScreen = new Point((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2),
            (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2));

    /**
     * Robot that is used for locking the mouse
     */
    private static Robot mouseBot;
    /**
     * Whether the mouse should be locked in the center of the screen
     */
    private boolean lockMouse;

    /**
     * Creates a camera
     * @param fov FIeld of view
     * @param mouseSensitivity Mouse sensitivity
     * @param nearPlain Near plain's distance
     * @param farPlain Far plain's distance
     */
    public Camera(double fov, double mouseSensitivity, double nearPlain, double farPlain)
    {
        this.position = new Vec3(0.0);
        this.worldUp = new Vec3(0.0, 1.0, 0.0);
        this.front = new Vec3(0.0, 0.0, -1.0);
        this.right = new Vec3(1.0, 0.0, 0.0);
        this.up = new Vec3(0.0, 1.0, 0.0);
        this.yaw = this.pitch = 0.0;
        this.moveSpeed = 10.0;
        this.mouseSensitivity = mouseSensitivity;
        this.projection = Meth.perspective(fov, (double)Renderer.getWidth() / Renderer.getHeight(), nearPlain, farPlain);
        //this.projection = Math.Meth.orthographic(-10, 10, 10, -10, 0, 10);

        try { mouseBot = new Robot(); } catch (Exception e) { e.printStackTrace(); }
        this.lockMouse = false;
    }

    /**
     * Updates the camera (moves with WASD, looks at mouse)
     */
    public void update(double dt)
    {
        short dx = 0, dz = 0;
        if (Input.isKeyDown(KeyEvent.VK_W)) dz +=  1;
        if (Input.isKeyDown(KeyEvent.VK_A)) dx += -1;
        if (Input.isKeyDown(KeyEvent.VK_S)) dz += -1;
        if (Input.isKeyDown(KeyEvent.VK_D)) dx +=  1;
        position = position.add(new Vec3(front.getX(), 0.0, front.getZ()).scale(dz * moveSpeed * dt));
        position = position.add(new Vec3(right.getX(), 0.0, right.getZ()).scale(dx).scale(moveSpeed * dt));

        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        // mouse is on the screen
        if (mousePos != null)
        {
            Point mouseDelta = new Point(mousePos.x - centerScreen.x, mousePos.y - centerScreen.y);
            if (lockMouse)
                mouseBot.mouseMove(centerScreen.x, centerScreen.y);
            rotate(new Vec2(mouseDelta.x, mouseDelta.y).scale(-mouseSensitivity * dt));
        }
    }

    /**
     * Sets the position of the camera
     */
    public void setPosition(Vec3 position) { this.position = position; }
    /**
     * Rotates the camera
     */
    public void rotate(Vec2 rotation) { yaw += rotation.getX(); yaw %= 360.0; pitch = Math.clamp(pitch + rotation.getY(), -90.0, 90.0); }

    /**
     * Looks at a point in world space
     */
    public void lookAt(Vec3 point)
    {
        Vec3 direction = point.subtract(position).normalize();

        int yawSign = direction.getX() >= 0 ? 1 : -1;
        int pitchSign = direction.getY() >= 0 ? 1 : -1;
        double clampValue = 1.0 - Math.ulp(1.0);
        yaw = yawSign * Math.toDegrees(Math.acos(Math.clamp(new Vec3(0.0, direction.getY(), direction.getZ()).normalize().dot(direction), -clampValue, clampValue)));
        pitch = pitchSign * Math.toDegrees(Math.clamp(Math.acos(new Vec3(direction.getX(), 0.0, direction.getZ()).normalize().dot(direction)), -clampValue, clampValue));
    }

    /**
     * Sets whether the mouse should be locked
     */
    public void setMouseLock(boolean lockMouse) { this.lockMouse = lockMouse; }

    /**
     * Returns the position of the camera
     */
    public Vec3 getPosition() { return position; }

    /**
     * Returns the projection matrix of the camera
     */
    public Mat4 getProjectionMatrix() { return projection; }
    /**
     * Returns the view matrix of the camera
     */
    public Mat4 getViewMatrix()
    {
        updateData();
        return view;
    }

    /**
     * Updates the camera vectors and matrices based on the set data (rotation and other things)
     */
    private void updateData()
    {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        front = new Vec3(
                -Math.sin(yawRad) * Math.cos(pitchRad),
                    Math.sin(pitchRad),
                -Math.cos(yawRad) * Math.cos(pitchRad)
        ).normalize();
        right = front.cross(worldUp).normalize();
        up = right.cross(front).normalize();

        view = Meth.lookAt(position, front, up);
    }
}
