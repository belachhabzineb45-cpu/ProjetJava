import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Theme {
    public static final Color BG = new Color(244, 247, 251);
    public static final Color CARD = Color.WHITE;
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_DARK = new Color(30, 64, 175);
    public static final Color TEXT = new Color(31, 41, 55);
    public static final Color MUTED = new Color(107, 114, 128);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SUBTITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font BUTTON = new Font("Segoe UI", Font.BOLD, 15);

    public static void setupFrame(JFrame frame, String title, int w, int h) {
        frame.setTitle(title);
        frame.setSize(w, h);
        frame.setMinimumSize(new Dimension(w, h));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG);
    }

    public static JPanel card(int padding) {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(padding, padding, padding, padding)
        ));
        return p;
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BUTTON);
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        return b;
    }

    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BUTTON);
        b.setBackground(new Color(229, 231, 235));
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        return b;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(NORMAL);
        l.setForeground(TEXT);
        return l;
    }

    public static void styleField(JTextField f) {
        f.setFont(NORMAL);
        f.setForeground(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
    }
}
