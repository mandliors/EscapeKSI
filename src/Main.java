import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame
{
    private final int WIDTH = 1800;
    private final int HEIGHT = 1200;

    public Main() { init(); }

    private void init()
    {
        setTitle("Escape KSI");
        setSize(WIDTH, HEIGHT);
        //setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Game game = new Game();
        add(game);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                game.init();
            }
        });
    }


    public static void main(String[] args)
    {
        new Main().setVisible(true);
    }
}