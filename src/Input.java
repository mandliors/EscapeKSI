import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Robot;

public class Input
{
    // canvas on which the input is received
    private static Canvas canvas;

    // robot needed for centering the mouse on screen
    private static Robot mouseBot;

    // codes for all important keys and mouse buttons are below VK_F12 and BUTTON1
    private static boolean previousKeyboardState[] = new boolean[KeyEvent.VK_F12 + 1];
    private static boolean previousMouseButtonState[] = new boolean[MouseEvent.BUTTON3 + 1];
    private static boolean keyboardState[] = new boolean[KeyEvent.VK_F12 + 1];
    private static boolean mouseButtonState[] = new boolean[MouseEvent.BUTTON3 + 1];

    // stored mouse motion until it is queried (then it's reset to (0, 0))
    private static Point mousePosition = new Point(0, 0);

    static {
        for (int i = 0; i < previousKeyboardState.length; i++)
            previousKeyboardState[i] = false;
        for (int i = 0; i < previousMouseButtonState.length; i++)
            previousMouseButtonState[i] = false;
        for (int i = 0; i < keyboardState.length; i++)
            keyboardState[i] = false;
        for (int i = 0; i < mouseButtonState.length; i++)
            mouseButtonState[i] = false;
    }

    public static void init(Canvas canvas) throws NullPointerException
    {
        Input.canvas = canvas;
        try { mouseBot = new Robot(); } catch (Exception e) { e.printStackTrace(); }

        Point mousePos = canvas.getMousePosition();
        if (mousePos == null)
            throw new NullPointerException("mouse position is null");

        mousePosition = new Point(mousePos.x, mousePos.y);

        canvas.requestFocus();
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() < keyboardState.length) keyboardState[e.getKeyCode()] = true; }
            @Override
            public void keyReleased(KeyEvent e) { if (e.getKeyCode() < keyboardState.length) keyboardState[e.getKeyCode()] = false; }
        });
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { if (e.getButton() < mouseButtonState.length) mouseButtonState[e.getButton()] = true; }
            @Override
            public void mouseReleased(MouseEvent e) { if (e.getButton() < mouseButtonState.length) mouseButtonState[e.getButton()] = false; }
        });
    }

    public static void update()
    {
        // save the states
        for (int i = 0; i < keyboardState.length; i++)
            previousKeyboardState[i] = keyboardState[i];
        for (int i = 0; i < mouseButtonState.length; i++)
            previousMouseButtonState[i] = mouseButtonState[i];

        // reset the mouse position
        mouseBot.mouseMove(canvas.getLocationOnScreen().x + canvas.getWidth() / 2, canvas.getLocationOnScreen().y + canvas.getHeight() / 2);
        mousePosition = canvas.getMousePosition();
        if (mousePosition == null)
            mousePosition = new Point(0, 0);
    }

    // was it just pressed?
    public static boolean isKeyPressed(int key) { return key < keyboardState.length && keyboardState[key] && !previousKeyboardState[key]; }
    public static boolean isMouseButtonPressed(int button) { return button < mouseButtonState.length && mouseButtonState[button] && !previousMouseButtonState[button]; }

    // was it just released?
    public static boolean isKeyReleased(int key) { return key < keyboardState.length && !keyboardState[key] && previousKeyboardState[key]; }
    public static boolean isMouseButtonReleased(int button) { return button < mouseButtonState.length && !mouseButtonState[button] && previousMouseButtonState[button]; }

    // is it being held?
    public static boolean isKeyDown(int key) { return key < keyboardState.length && keyboardState[key]; }
    public static boolean isMouseButtonDown(int button) { return button < mouseButtonState.length && mouseButtonState[button]; }

    // mouse delta from the previous frame
    public static Vec2 getMouseDelta()
    {
        Point mousePos = canvas.getMousePosition();
        if (mousePos == null)
            mousePos = new Point(0, 0);

        return new Vec2(mousePos.x - mousePosition.x, mousePos.y - mousePosition.y);
    }
}
