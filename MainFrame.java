package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("杂志订阅管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);

        buildUI();
    }

    private void buildUI() {
        // 顶部标题栏
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 60, 120));
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel title = new JLabel("杂志订阅管理系统");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("微软雅黑", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        JLabel subTitle = new JLabel("Magazine Subscription Management System");
        subTitle.setForeground(new Color(180, 200, 240));
        subTitle.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        header.add(subTitle, BorderLayout.EAST);

        // 选项卡
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        tabs.setBackground(new Color(245, 247, 250));
        tabs.setFocusable(false);

        MagazinePanel    magPanel  = new MagazinePanel();
        SubscriberPanel  perPanel  = new SubscriberPanel();
        SubscriptionPanel subPanel = new SubscriptionPanel();

        tabs.addTab("杂志管理",   magPanel);
        tabs.addTab("订阅者管理",   perPanel);
        tabs.addTab("订阅记录",    subPanel);

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 2) {
                subPanel.reloadCombos();
            }
        });

        // 底部状态栏
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(235, 238, 245));
        footer.setPreferredSize(new Dimension(0, 28));
        footer.setBorder(new EmptyBorder(0, 16, 0, 16));

        JLabel status = new JLabel("就绪");
        status.setForeground(Color.GRAY);
        status.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        footer.add(status, BorderLayout.WEST);

        JLabel version = new JLabel("v1.0  |  数据存储：SQLite");
        version.setForeground(Color.LIGHT_GRAY);
        version.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        footer.add(version, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(tabs,   BorderLayout.CENTER);
        getContentPane().add(footer, BorderLayout.SOUTH);
    }
}
