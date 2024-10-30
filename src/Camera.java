public class Camera
{
    private Vec3 position, worldUp;
    private Vec3 front, right, up;
    private double yaw, pitch;
    //private double fov, aspectRatio;
    //private double nearPlain, farPlain;
    private double moveSpeed, mouseSensitivity;

    private Mat4 projection, view;

    public Camera(double fov, double aspectRatio, double nearPlain, double farPlain)
    {
        this.position = new Vec3(0.0);
        this.worldUp = new Vec3(0.0, 1.0, 0.0);
        this.front = new Vec3(0.0, 0.0, -1.0);
        this.right = new Vec3(1.0, 0.0, 0.0);
        this.up = new Vec3(0.0, 1.0, 0.0);
        this.yaw = this.pitch = 0.0;
        //this.fov = fov;
        //this.aspectRatio = aspectRatio;
        //this.nearPlain = nearPlain;
        //this.farPlain = farPlain;
        this.moveSpeed = 40.0;
        this.mouseSensitivity = 0.01;

        this.projection = Meth.perspective(fov, aspectRatio, nearPlain, farPlain);
        //this.projection = Meth.orthographic(-10, 10, 10, -10, 0, 10);
    }

    public void translate(Vec2 translation) { position = position.add(new Vec3(translation.getX() * moveSpeed, 0.0, translation.getY() * moveSpeed)); }
    public void rotate(Vec2 rotation) { yaw += rotation.getX(); pitch += rotation.getY(); }

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
