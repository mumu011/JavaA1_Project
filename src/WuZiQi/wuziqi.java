package WuZiQi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class wuziqi extends JFrame {
    Display display = new Display();
    Board board = new Board(display);
    Control control = new Control(board);

    public wuziqi() {
        super("五子棋");
        setVisible(true);
        setSize(850,1000);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setFocusable(true);

        JPanel contentPanel = (JPanel) getContentPane();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(board, "Center");
        contentPanel.add(control.getPanel(), "South");
        contentPanel.add(display.getPanel(), "North");

    }

    public static void main(String[] args) {
        wuziqi wuziqi = new wuziqi();
    }
}
