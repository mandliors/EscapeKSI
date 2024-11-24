package main.java;

import main.java.leaderboard.Leaderboard;
import main.java.screens.GameScreen;
import main.java.screens.LeaderboardScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class App extends JFrame
{
    enum GameState { MAIN_MENU, GAME, LEADERBOARD }

    private static final int WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final int HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    GameState gameState;
    JPanel mainPanel;

    public App()
    {
        super("Escape KSI");
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        Leaderboard.loadFromFile("res/leaderboard.dat");

        JFrame thisFrame = this;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Leaderboard.saveToFile("res/leaderboard.dat");
                System.exit(0);
            }
        });

        // panel for the UI components
        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // create buttons
        JButton gameButton = createDarkButton("Start Game");
        JButton leaderboard = createDarkButton("Leaderboard");
        JButton exitButton = createDarkButton("Exit");

        // add action listeners
        gameButton.addActionListener(e -> gameState = GameState.GAME );
        leaderboard.addActionListener(e -> gameState = GameState.LEADERBOARD );
        exitButton.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        // add buttons to the panel with spacing
        Dimension spacing = new Dimension(0, 40);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(gameButton);
        mainPanel.add(Box.createRigidArea(spacing));
        mainPanel.add(leaderboard);
        mainPanel.add(Box.createRigidArea(spacing));
        mainPanel.add(exitButton);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
        setVisible(true);

        gameState = GameState.MAIN_MENU;

        display();
    }

    public void display()
    {
        while (true)
        {
            switch (gameState)
            {
                case MAIN_MENU -> {
                    try { Thread.sleep(10); } catch (Exception e) { e.printStackTrace(); }
                }
                case GAME -> {
                    GameScreen gameScreen = new GameScreen(this, WIDTH, HEIGHT);
                    gameScreen.display();
                    backToMainMenu();
                }
                case LEADERBOARD -> {
                    LeaderboardScreen leaderboardScreen = new LeaderboardScreen(this, WIDTH, HEIGHT);
                    leaderboardScreen.display();
                    backToMainMenu();
                }
            }
        }
    }

    private void backToMainMenu()
    {
        gameState = GameState.MAIN_MENU;
        setContentPane(mainPanel);
        revalidate();
        repaint();
    }

    private JButton createDarkButton(String text)
    {
        JButton button = new JButton(text);

        button.setBackground(new Color(40, 40, 40));
        button.setForeground(new Color(220, 220, 220));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(20, 60, 20, 60)
        ));
        button.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 40));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }

    public static void main(String[] args)
    {
        new App();
    }
}
