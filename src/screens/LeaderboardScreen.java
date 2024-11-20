package screens;

import leaderboard.Leaderboard;
import input.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class LeaderboardScreen extends JPanel
{
    public LeaderboardScreen(JFrame parent, int width, int height)
    {
        setSize(width, height);
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));
        parent.setContentPane(this);

        // initialize input for this panel
        Input.init(this);

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

        parent.revalidate();
        parent.repaint();
    }

    public void display()
    {
        while (!Input.isKeyPressed(KeyEvent.VK_ESCAPE))
        {
            Input.update();
            grabFocus();

            try { Thread.sleep(10); } catch (Exception e) { e.printStackTrace(); }
        }
    }
}