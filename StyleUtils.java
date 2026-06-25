package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class StyleUtils {

    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(210, 225, 250));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        table.setBackground(Color.WHITE);

        DefaultTableCellRenderer zebra = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,  
                                                            boolean isSelected, boolean hasFocus,
                                                            int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        setBackground(Color.WHITE);
                    } else {
                        setBackground(new Color(248, 249, 252));
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(zebra);
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(60, 90, 150));
        header.setForeground(Color.WHITE);
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 32));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        header.setDefaultRenderer(centerRenderer);
    }
}
