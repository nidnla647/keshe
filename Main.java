import dao.DBHelper;
import ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 初始化数据库
        DBHelper.initTables();

        // 启动 Swing UI（在 EDT 线程）
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // 使用系统外观
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {}

                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}
