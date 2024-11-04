import java.awt.*;
import java.awt.event.KeyEvent;

public class Camera
{
    private Vec3 position, worldUp;
    private Vec3 front, right, up;
    private double yaw, pitch;
    private double moveSpeed, mouseSensitivity;

    private Mat4 projection, view;

    private Point previousMousePosition;

    // needed for locking the mouse
    private Robot mouseBot;
    private Canvas canvas;
    private boolean lockMouse;

    public Camera(double fov, double aspectRatio, double nearPlain, double farPlain)
    {
        this.position = new Vec3(0.0);
        this.worldUp = new Vec3(0.0, 1.0, 0.0);
        this.front = new Vec3(0.0, 0.0, -1.0);
        this.right = new Vec3(1.0, 0.0, 0.0);
        this.up = new Vec3(0.0, 1.0, 0.0);
        this.yaw = this.pitch = 0.0;
        this.moveSpeed = 2.0;
        this.mouseSensitivity = 0.05;
        this.projection = Meth.perspective(fov, aspectRatio, nearPlain, farPlain);
        //this.projection = Meth.orthographic(-10, 10, 10, -10, 0, 10);

        this.previousMousePosition = new Point(0, 0);
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
        position = position.add(front.scale(dz * moveSpeed * dt));
        position = position.add(right.scale(dx * moveSpeed * dt));

        Point mousePos = Input.getMousePosition();
        // mouse is on the screen
        if (mousePos != null)
        {
            Point mouseDelta = new Point(mousePos.x - previousMousePosition.x, mousePos.y - previousMousePosition.y);
            if (lockMouse)
                mouseBot.mouseMove(canvas.getLocationOnScreen().x + canvas.getWidth() / 2 ,canvas.getLocationOnScreen().y + canvas.getHeight() / 2);
            previousMousePosition = mousePos;
            rotate(new Vec2(mouseDelta.x, mouseDelta.y).scale(-mouseSensitivity));
        }
    }

    public void translate(Vec2 translation) { position = position.add(new Vec3(translation.getX() * moveSpeed, 0.0, translation.getY() * moveSpeed)); }
    public void rotate(Vec2 rotation) { yaw += rotation.getX(); yaw %= 360.0; pitch = Math.clamp(pitch + rotation.getY(), -90.0, 90.0); }

    public void setMouseLock(Canvas canvas, boolean lockMouse) { this.canvas = canvas; this.lockMouse = lockMouse; }

    public Vec3 getPosition() { return position; }
    public Mat4 getProjection() { return projection; }
    public Mat4 getView()
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
