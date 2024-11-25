package main.java.screens;

import main.java.input.Input;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class SettingsScreen extends JPanel
{
    /**
     * Whether settings screen should be shown
     */
    private boolean exit = false;

    /**
     * Creates a new settings screen on the parent frame with the given dimensions
     */
    public SettingsScreen(JFrame parent, int width, int height)
    {
        setSize(width, height);
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));
        setFocusable(false);
        parent.setContentPane(this);

        setBorder(new EmptyBorder(40, 40, 40, 40));

        // initialize input for the parent
        Input.init(parent);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 40, 40));
        panel.setLayout(new GridLayout(0, 2));

        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 40);

        // settings label
        JLabel settingsLabel = new JLabel("Settings");
        settingsLabel.setForeground(new Color(220, 220, 220));
        settingsLabel.setFont(font);
        settingsLabel.setFocusable(false);
        panel.add(settingsLabel);

        // placeholder
        JPanel placeholderPanel = new JPanel();
        placeholderPanel.setOpaque(false);
        panel.add(placeholderPanel);

        // fov label
        JLabel fovLabel = new JLabel("FOV:");
        fovLabel.setForeground(new Color(220, 220, 220));
        fovLabel.setFont(font);
        fovLabel.setFocusable(false);
        panel.add(fovLabel);

        // fov slider
        JSlider fovSlider = new JSlider(30, 90, (int)GameScreen.FOV);
        fovSlider.setBackground(new Color(40, 40, 40));
        fovSlider.setForeground(new Color(220, 220, 220));
        fovSlider.setMajorTickSpacing(1);
        fovSlider.setPaintTicks(true);
        fovSlider.setPaintLabels(true);
        fovSlider.setFocusable(false);
        fovSlider.addChangeListener(e -> {
            GameScreen.FOV = (double) fovSlider.getValue();
        });
        panel.add(fovSlider);

        // mouse sensitivity label
        JLabel mouseSensLabel = new JLabel("Mouse Sensitivity:");
        mouseSensLabel.setForeground(new Color(220, 220, 220));
        mouseSensLabel.setFont(font);
        mouseSensLabel.setFocusable(false);
        panel.add(mouseSensLabel);

        // add a slider
        JSlider mouseSensSlider = new JSlider(1, 100, (int)(GameScreen.mouseSensitivity * 100));
        mouseSensSlider.setBackground(new Color(40, 40, 40));
        mouseSensSlider.setForeground(new Color(220, 220, 220));
        mouseSensSlider.setMajorTickSpacing(1);
        mouseSensSlider.setPaintTicks(true);
        mouseSensSlider.setPaintLabels(false);
        mouseSensSlider.setFocusable(false);
        mouseSensSlider.addChangeListener(e -> {
            GameScreen.mouseSensitivity = mouseSensSlider.getValue() / 100.0;
        });
        panel.add(mouseSensSlider);

        // maze label
        JLabel mazeLabel = new JLabel("Theme:");
        mazeLabel.setForeground(new Color(220, 220, 220));
        mazeLabel.setFont(font);
        mazeLabel.setFocusable(false);
        panel.add(mazeLabel);

        // load maze names
        String[] mazes = null;
        File folder = new File("res/mazes");
        if (folder.exists() && folder.isDirectory())
        {
            File[] subfolders = folder.listFiles(File::isDirectory);
            if (subfolders != null)
            {
                mazes = new String[subfolders.length];
                for (int i = 0; i < subfolders.length; i++)
                {
                    File subfolder = subfolders[i];
                    mazes[i] = subfolder.getName();
                }
            }
        }

        // setup dropdown
        JComboBox<String> mazeDropdown = new JComboBox<>(mazes == null ? new String[] { "" } : mazes);
        mazeDropdown.addActionListener(e -> {
            GameScreen.selectedMaze = (String) mazeDropdown.getSelectedItem();
        });
        mazeDropdown.setBackground(new Color(40, 40, 40));
        mazeDropdown.setForeground(new Color(220, 220, 220));
        mazeDropdown.setFont(font);
        mazeDropdown.setFocusable(false);
        mazeDropdown.setMaximumSize(new Dimension(width - 40, 80));
        mazeDropdown.setPreferredSize(new Dimension(width - 40, 80));
        mazeDropdown.setSelectedItem(GameScreen.selectedMaze);
        panel.add(mazeDropdown);

        add(panel, BorderLayout.NORTH);

        parent.revalidate();
        parent.repaint();
    }

    /**
     * Starts the main loop fot the settings panel
     */
    public void display()
    {
        while (!exit)
        {
            if (Input.isKeyPressed(KeyEvent.VK_ESCAPE))
                exit = true;

            Input.update();

            try { Thread.sleep(10); } catch (Exception e) { e.printStackTrace(); }
        }
    }
}