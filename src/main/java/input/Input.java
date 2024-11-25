package main.java.input;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class Input
{
    /**
     * The keyboard state from the previous frame
     */
    private static final boolean[] previousKeyboardState = new boolean[KeyEvent.KEY_LAST + 1];
    /**
     * The mouse button state from the previous frame
     */
    private static final boolean[] previousMouseButtonState = new boolean[MouseEvent.BUTTON3 + 1];
    /**
     * The keyboard state in the current frame
     */
    private static final boolean[] keyboardState = new boolean[KeyEvent.KEY_LAST + 1];
    /**
     * The mouse button state in the current frame
     */
    private static final boolean[] mouseButtonState = new boolean[MouseEvent.BUTTON3 + 1];

    /**
     * Initializes the keyboard and mouse button states
     */
    public static void init(JFrame frame)
    {
        Arrays.fill(previousKeyboardState, false);
        Arrays.fill(previousMouseButtonState, false);
        Arrays.fill(keyboardState, false);
        Arrays.fill(mouseButtonState, false);

        frame.requestFocus();
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() < keyboardState.length) keyboardState[e.getKeyCode()] = true; }
            @Override
            public void keyReleased(KeyEvent e) { if (e.getKeyCode() < keyboardState.length) keyboardState[e.getKeyCode()] = false; }
        });
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { if (e.getButton() < mouseButtonState.length) mouseButtonState[e.getButton()] = true; }
            @Override
            public void mouseReleased(MouseEvent e) { if (e.getButton() < mouseButtonState.length) mouseButtonState[e.getButton()] = false; }
        });
    }

    /**
     * Invalidates the keyboard and mouse button states (previous state becomes the current state)
     */
    public static void update()
    {
        // save the states
        System.arraycopy(keyboardState, 0, previousKeyboardState, 0, keyboardState.length);
        System.arraycopy(mouseButtonState, 0, previousMouseButtonState, 0, mouseButtonState.length);
    }

    /**
     * Returns if the key has just been pressed
     */
    public static boolean isKeyPressed(int key) { return keyboardState[key] && !previousKeyboardState[key]; }
    /**
     * Returns if the mouse button has just been pressed
     */
    public static boolean isMouseButtonPressed(int button) { return button < mouseButtonState.length && mouseButtonState[button] && !previousMouseButtonState[button]; }

    /**
     * Returns if the key has just been released
     */
    public static boolean isKeyReleased(int key) { return !keyboardState[key] && previousKeyboardState[key]; }
    /**
     * Returns if the mouse button has just been released
     */
    public static boolean isMouseButtonReleased(int button) { return button < mouseButtonState.length && !mouseButtonState[button] && previousMouseButtonState[button]; }

    /**
     * Returns if the key is being pressed
     */
    public static boolean isKeyDown(int key) { return keyboardState[key]; }
    /**
     * Returns if the mouse button is being pressed
     */
    public static boolean isMouseButtonDown(int button) { return button < mouseButtonState.length && mouseButtonState[button]; }
}
