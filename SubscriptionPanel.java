package ui;

import dao.MagazineDAO;
import dao.SubscriberDAO;
import dao.SubscriptionDAO;
import model.Magazine;
import model.Subscriber;
import model.Subscription;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class SubscriptionPanel extends JPanel {
    private SubscriptionDAO subDao   = new SubscriptionDAO();
    private SubscriberDAO   perDao   = new SubscriberDAO();
    private MagazineDAO     magDao   = new MagazineDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfSearch;

    private JComboBox<Subscriber> cbSubscriber;
    private JComboBox<Magazine>   cbMagazine;
    private JSpinner              spMonths;
    private JLabel                lblTotal;

    private static final String[] COLUMNS = {
            "ID", "订阅者", "杂志", "开始日期", "到期日期", "月数", "总金额(元)", "状态"
    };

    public SubscriptionPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(245, 247, 250));
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.EAST);
        refreshTable(subDao.findAll());
    }

    private JPanel buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setOpaque(false);
        tfSearch = new JTextField(16);
        JButton btnSearch = StyledButton.primary("搜索");
        JButton btnReset  = StyledButton.secondary("重置");
        btnSearch.addActionListener(e -> {
            String kw = tfSearch.getText().trim();
            if (kw.isEmpty()) {
                refreshTable(subDao.findAll());
            } else {
                refreshTable(subDao.search(kw));
            }
        });
        btnReset.addActionListener(e -> { tfSearch.setText(""); refreshTable(subDao.findAll()); });
        p.add(new JLabel("关键字："));
        p.add(tfSearch);
        p.add(btnSearch);
        p.add(btnReset);
        return p;
    }

    private JScrollPane buildCenter() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        StyleUtils.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // 状态列着色
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                            boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    String s = String.valueOf(v);
                    if ("有效".equals(s)) {
                        setForeground(new Color(34, 139, 34));
                    } else if ("已到期".equals(s)) {
                        setForeground(new Color(180, 80, 0));
                    } else if ("已取消".equals(s)) {
                        setForeground(Color.GRAY);
                    } else {
                        setForeground(Color.BLACK);
                    }
                }
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 225)));
        return sp;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(260, 0));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                new EmptyBorder(12, 12, 12, 12)));
        p.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("新增订阅");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 13f));
        p.add(titleLabel);
        p.add(Box.createVerticalStrut(10));

        cbSubscriber = new JComboBox<Subscriber>();
        cbMagazine   = new JComboBox<Magazine>();
        loadCombos();

        addFormRow(p, "订阅者 *", cbSubscriber);
        addFormRow(p, "杂志 *",   cbMagazine);

        spMonths = new JSpinner(new SpinnerNumberModel(1, 1, 120, 1));
        spMonths.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        addFormRow(p, "订阅月数", spMonths);

        lblTotal = new JLabel("总金额：--");
        lblTotal.setForeground(new Color(200, 60, 60));
        p.add(lblTotal);
        p.add(Box.createVerticalStrut(8));

        // 监听变化自动计算金额
        cbMagazine.addActionListener(e -> calcTotal());
        spMonths.addChangeListener(e -> calcTotal());
        calcTotal();

        JButton btnAdd = StyledButton.primary("确认订阅");
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdd.addActionListener(e -> doInsert());
        p.add(btnAdd);

        p.add(Box.createVerticalStrut(20));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.add(sep);
        p.add(Box.createVerticalStrut(12));

        JLabel titleLabel2 = new JLabel("操作选中记录");
        titleLabel2.setFont(titleLabel2.getFont().deriveFont(Font.BOLD, 13f));
        p.add(titleLabel2);
        p.add(Box.createVerticalStrut(10));

        JButton btnCancel = StyledButton.danger("取消订阅");
        JButton btnExpire = StyledButton.secondary("标记到期");
        JButton btnDelete = StyledButton.secondary("删除记录");

        btnCancel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btnExpire.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btnDelete.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        btnCancel.addActionListener(e -> doChangeStatus("已取消"));
        btnExpire.addActionListener(e -> doChangeStatus("已到期"));
        btnDelete.addActionListener(e -> doDelete());

        p.add(btnCancel);
        p.add(Box.createVerticalStrut(6));
        p.add(btnExpire);
        p.add(Box.createVerticalStrut(6));
        p.add(btnDelete);

        p.add(Box.createVerticalGlue());
        return p;
    }

    private void addFormRow(JPanel p, String label, JComponent field) {
        p.add(new JLabel(label));
        p.add(Box.createVerticalStrut(3));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        p.add(field);
        p.add(Box.createVerticalStrut(8));
    }

    private void loadCombos() {
        cbSubscriber.removeAllItems();
        cbMagazine.removeAllItems();
        List<Subscriber> subscribers = perDao.findAll();
        for (Subscriber s : subscribers) {
            cbSubscriber.addItem(s);
        }
        List<Magazine> magazines = magDao.findAll();
        for (Magazine m : magazines) {
            cbMagazine.addItem(m);
        }
    }

    private void calcTotal() {
        Magazine m = (Magazine) cbMagazine.getSelectedItem();
        int months = (Integer) spMonths.getValue();
        if (m != null) {
            double total = m.getPrice() * months;
            lblTotal.setText(String.format("总金额：%.2f 元", total));
        } else {
            lblTotal.setText("总金额：--");
        }
    }

    private void doInsert() {
        Subscriber per = (Subscriber) cbSubscriber.getSelectedItem();
        Magazine   mag = (Magazine)    cbMagazine.getSelectedItem();
        if (per == null || mag == null) {
            JOptionPane.showMessageDialog(this, "请先添加订阅者和杂志。"); return;
        }
        int months = (Integer) spMonths.getValue();

        // 计算日期
        Calendar cal = Calendar.getInstance();
        String startDate = String.format("%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.MONTH, months);
        String endDate = String.format("%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));

        double total = mag.getPrice() * months;

        Subscription sub = new Subscription(0, per.getId(), mag.getId(),
                per.getName(), mag.getName(),
                startDate, endDate,
                months, total, "有效");
        if (subDao.insert(sub)) {
            JOptionPane.showMessageDialog(this, "订阅成功！");
            refreshTable(subDao.findAll());
        } else {
            JOptionPane.showMessageDialog(this, "订阅失败，请重试。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doChangeStatus(String status) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行。"); return; }
        int id = (Integer) tableModel.getValueAt(row, 0);
        if (subDao.updateStatus(id, status)) {
            refreshTable(subDao.findAll());
        }
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行。"); return; }
        int id = (Integer) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确认删除该订阅记录？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            subDao.delete(id);
            refreshTable(subDao.findAll());
        }
    }

    public void reloadCombos() {
        loadCombos();
    }

    private void refreshTable(List<Subscription> list) {
        tableModel.setRowCount(0);
        for (Subscription s : list) {
            tableModel.addRow(new Object[]{
                    s.getId(), s.getSubscriberName(), s.getMagazineName(),
                    s.getStartDate(), s.getEndDate(), s.getMonths(),
                    String.format("%.2f", s.getTotalPrice()), s.getStatus()
            });
        }
    }
}
