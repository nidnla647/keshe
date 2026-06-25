package ui;

import javax.swing.*;
import java.awt.*;

public class StyledButton {

    private static final Color PRIMARY   = new Color(70, 130, 210);
    private static final Color SECONDARY = new Color(100, 110, 125);
    private static final Color DANGER    = new Color(210, 60, 60);

    public static JButton primary(String text) {
        return make(text, PRIMARY);
    }

    public static JButton secondary(String text) {
        return make(text, SECONDARY);
    }

    public static JButton danger(String text) {
        return make(text, DANGER);
    }

    private static JButton make(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
                if (getModel().isPressed()) { 
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 12f));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(72, 28));
        return btn;
    }
}
