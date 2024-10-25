import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Input
{
    // codes for all important keys and mouse buttons are below VK_F12 and BUTTON1
    private static boolean keyboardState[] = new boolean[KeyEvent.VK_F12 + 1];
    private static boolean mouseButtonState[] = new boolean[MouseEvent.BUTTON3 + 1];

    // stored mouse motion until it is queried (then it's reset to (0, 0))
    private static Vec2 mouseDelta = new Vec2(0.0, 0.0);

    static {
        for (int i = 0; i < keyboardState.length; i++)
            keyboardState[i] = false;
        for (int i = 0; i < mouseButtonState.length; i++)
            mouseButtonState[i] = false;
    }

    public static boolean isKeyDown(int key) { return key < keyboardState.length && keyboardState[key]; }
    public static boolean isMouseButtonDown(int button) { return button < mouseButtonState.length && mouseButtonState[button]; }

    public static void setKeyDown(int key, boolean value) { if (key >= keyboardState.length) return; keyboardState[key] = value; }
    public static void setMouseButtonDown(int button, boolean value) { if (button >= mouseButtonState.length) return; mouseButtonState[button] = value; }

    public static void addMouseDelta(Vec2 delta) { mouseDelta = mouseDelta.add(delta); }
    public static Vec2 getMouseDelta() { Vec2 delta = new Vec2(mouseDelta.getX(), mouseDelta.getY()); mouseDelta = new Vec2(0.0); return delta; }
}
