package main.java.screens;

import main.java.input.Input;
import main.java.leaderboard.Leaderboard;
import main.java.input.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class LeaderboardScreen extends JPanel
{
    /**
     * Creates a new leaderboard screen on the parent frame with the given dimensions
     */
    public LeaderboardScreen(JFrame parent, int width, int height)
    {
        setSize(width, height);
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));
        setFocusable(false);
        parent.setContentPane(this);

        // load the data
        List<Leaderboard.Record> records = Leaderboard.getRecords();

        String[] columns = { "Rank", "Name", "Time" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        Font tableFont = new Font(Font.MONOSPACED, Font.PLAIN, 40);

        // setup table
        JTable table = new JTable(tableModel);
        table.setRowHeight(60);
        table.setBackground(new Color(40, 40, 40));
        table.setForeground(new Color(220, 220, 220));
        table.setFont(tableFont);
        table.setBorder(new EmptyBorder(20,20, 20, 20));
        table.setRowSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setFocusable(false);

        // setup table header
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(new Color(40, 40, 40));
        header.setForeground(new Color(220, 220, 220));
        header.setFont(tableFont);

        int rank = 1;
        for (Leaderboard.Record record : records)
            tableModel.addRow(new Object[] { rank++, record.name, record.time });

        // add table to JScrollPane
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        tableScrollPane.setBackground(new Color(40, 40, 40));
        add(tableScrollPane, BorderLayout.CENTER);

        // initialize input for the parent
        Input.init(parent);

        parent.revalidate();
        parent.repaint();
    }

    /**
     * Starts the main loop of the leaderboard panel
     */
    public void display()
    {
        while (!Input.isKeyPressed(KeyEvent.VK_ESCAPE))
        {
            Input.update();

            try { Thread.sleep(10); } catch (Exception e) { e.printStackTrace(); }
        }
    }
}