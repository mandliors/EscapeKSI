package input;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class Input
{
    private static final boolean[] previousKeyboardState = new boolean[KeyEvent.KEY_LAST + 1];
    private static final boolean[] previousMouseButtonState = new boolean[MouseEvent.BUTTON3 + 1];
    private static final boolean[] keyboardState = new boolean[KeyEvent.KEY_LAST + 1];
    private static final boolean[] mouseButtonState = new boolean[MouseEvent.BUTTON3 + 1];

    public static void init(JPanel panel) throws NullPointerException
    {
        Arrays.fill(previousKeyboardState, false);
        Arrays.fill(previousMouseButtonState, false);
        Arrays.fill(keyboardState, false);
        Arrays.fill(mouseButtonState, false);

        panel.requestFocus();
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() < keyboardState.length) keyboardState[e.getKeyCode()] = true; }
            @Override
            public void keyReleased(KeyEvent e) { if (e.getKeyCode() < keyboardState.length) keyboardState[e.getKeyCode()] = false; }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { if (e.getButton() < mouseButtonState.length) mouseButtonState[e.getButton()] = true; }
            @Override
            public void mouseReleased(MouseEvent e) { if (e.getButton() < mouseButtonState.length) mouseButtonState[e.getButton()] = false; }
        });
    }

    public static void update()
    {
        // save the states
        System.arraycopy(keyboardState, 0, previousKeyboardState, 0, keyboardState.length);
        System.arraycopy(mouseButtonState, 0, previousMouseButtonState, 0, mouseButtonState.length);
    }

    // was it just pressed?
    public static boolean isKeyPressed(int key) { return keyboardState[key] && !previousKeyboardState[key]; }
    public static boolean isMouseButtonPressed(int button) { return button < mouseButtonState.length && mouseButtonState[button] && !previousMouseButtonState[button]; }

    // was it just released?
    public static boolean isKeyReleased(int key) { return !keyboardState[key] && previousKeyboardState[key]; }
    public static boolean isMouseButtonReleased(int button) { return button < mouseButtonState.length && !mouseButtonState[button] && previousMouseButtonState[button]; }

    // is it being held?
    public static boolean isKeyDown(int key) { return keyboardState[key]; }
    public static boolean isMouseButtonDown(int button) { return button < mouseButtonState.length && mouseButtonState[button]; }
}
