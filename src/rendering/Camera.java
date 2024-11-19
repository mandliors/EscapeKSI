package rendering;

import java.awt.*;
import java.awt.event.KeyEvent;
import math.*;
import input.Input;

public class Camera
{
    private Vec3 position, worldUp;
    private Vec3 front, right, up;
    private double yaw, pitch;
    private double moveSpeed, mouseSensitivity;

    private Mat4 projection, view;

    private static Point centerScreen = new Point((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2),
            (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2));

    // needed for locking the mouse
    private static Robot mouseBot;
    private boolean lockMouse;

    public Camera(double fov, double nearPlain, double farPlain)
    {
        this.position = new Vec3(0.0);
        this.worldUp = new Vec3(0.0, 1.0, 0.0);
        this.front = new Vec3(0.0, 0.0, -1.0);
        this.right = new Vec3(1.0, 0.0, 0.0);
        this.up = new Vec3(0.0, 1.0, 0.0);
        this.yaw = this.pitch = 0.0;
        this.moveSpeed = 10.0;
        this.mouseSensitivity = 0.06;
        this.projection = Meth.perspective(fov, (double)Renderer.getWidth() / Renderer.getHeight(), nearPlain, farPlain);
        //this.projection = Math.Meth.orthographic(-10, 10, 10, -10, 0, 10);

        try { mouseBot = new Robot(); } catch (Exception e) { e.printStackTrace(); }
        this.lockMouse = false;
    }

    public void update(double dt)
    {
        short dx = 0, dz = 0;
        if (Input.isKeyDown(KeyEvent.VK_W)) dz +=  1;
        if (Input.isKeyDown(KeyEvent.VK_A)) dx += -1;
        if (Input.isKeyDown(KeyEvent.VK_S)) dz += -1;
        if (Input.isKeyDown(KeyEvent.VK_D)) dx +=  1;
        position = position.add(new Vec3(front.getX(), 0.0, front.getZ()).scale(dz * moveSpeed * dt));
        position = position.add(new Vec3(right.getX(), 0.0, right.getZ()).scale(dx).scale(moveSpeed * dt));

//        position = position.add(front.scale(dz * moveSpeed * dt));
//        position = position.add(right.scale(dx * moveSpeed * dt));

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

    public void setPosition(Vec3 position) { this.position = position; }
    public void rotate(Vec2 rotation) { yaw += rotation.getX(); yaw %= 360.0; pitch = Math.clamp(pitch + rotation.getY(), -90.0, 90.0); }
    public void lookAt(Vec3 point)
    {
        Vec3 direction = point.subtract(position).normalize();

        int yawSign = direction.getX() >= 0 ? 1 : -1;
        int pitchSign = direction.getY() >= 0 ? 1 : -1;
        double clampValue = 1.0 - Math.ulp(1.0);
        yaw = yawSign * Math.toDegrees(Math.acos(Math.clamp(new Vec3(0.0, direction.getY(), direction.getZ()).normalize().dot(direction), -clampValue, clampValue)));
        pitch = pitchSign * Math.toDegrees(Math.clamp(Math.acos(new Vec3(direction.getX(), 0.0, direction.getZ()).normalize().dot(direction)), -clampValue, clampValue));
    }

    public void setMouseLock(boolean lockMouse) { this.lockMouse = lockMouse; }

    public Vec3 getPosition() { return position; }
    public Mat4 getProjectionMatrix() { return projection; }
    public Mat4 getViewMatrix()
    {
        updateData();
        return view;
    }

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
