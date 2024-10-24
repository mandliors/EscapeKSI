import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Input
{
    // codes for all important keys and mouse buttons are below VK_F12 and BUTTON1
    private static boolean keyboardState[] = new boolean[KeyEvent.VK_F12 + 1];
    private static boolean mouseButtonState[] = new boolean[MouseEvent.BUTTON3 + 1];

    static {
        for (int i = 0; i < keyboardState.length; i++)
            keyboardState[i] = false;
        for (int i = 0; i < mouseButtonState.length; i++)
            mouseButtonState[i] = false;
    }

    public boolean isKeyDown(int key) { return key < keyboardState.length && keyboardState[key]; }
    public boolean isMouseButtonDown(int button) { return button < mouseButtonState.length && mouseButtonState[button]; }

    public void setKeyDown(int key, boolean value) { if (key >= keyboardState.length) return; keyboardState[key] = value; }
    public void setMouseButtonDown(int button, boolean value) { if (button >= mouseButtonState.length) return; mouseButtonState[button] = value; }
}
