package ui;

import dao.SubscriberDAO;
import model.Subscriber;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SubscriberPanel extends JPanel {
    private SubscriberDAO dao = new SubscriberDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfSearch;
    private JTextField tfName, tfPhone, tfEmail, tfAddress;

    private static final String[] COLUMNS = {"ID", "姓名", "手机号", "邮箱", "收件地址"};

    public SubscriberPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(245, 247, 250));
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.EAST);
        refreshTable(dao.findAll());
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
                refreshTable(dao.findAll());
            } else {
                refreshTable(dao.search(kw));
            }
        });
        btnReset.addActionListener(e -> { tfSearch.setText(""); refreshTable(dao.findAll()); });
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
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
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

        tfName    = new JTextField();
        tfPhone   = new JTextField();
        tfEmail   = new JTextField();
        tfAddress = new JTextField();

        addFormRow(p, "姓名 *",   tfName);
        addFormRow(p, "手机号",   tfPhone);
        addFormRow(p, "邮箱",     tfEmail);
        addFormRow(p, "收件地址", tfAddress);

        p.add(Box.createVerticalGlue());

        JPanel btnRow = new JPanel(new GridLayout(1, 3, 6, 0));
        btnRow.setOpaque(false);
        JButton btnAdd    = StyledButton.primary("新增");
        JButton btnUpdate = StyledButton.secondary("修改");
        JButton btnDelete = StyledButton.danger("删除");
        btnAdd.addActionListener(e -> doInsert());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnRow.add(btnAdd);
        btnRow.add(btnUpdate);
        btnRow.add(btnDelete);
        p.add(btnRow);
        return p;
    }

    private void addFormRow(JPanel p, String label, JTextField field) {
        p.add(new JLabel(label));
        p.add(Box.createVerticalStrut(3));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        p.add(field);
        p.add(Box.createVerticalStrut(8));
    }

    private void doInsert() {
        Subscriber s = readForm(-1);
        if (s == null) return;
        if (dao.insert(s)) {
            JOptionPane.showMessageDialog(this, "新增成功！");
            clearForm();
            refreshTable(dao.findAll());
        } else {
            JOptionPane.showMessageDialog(this, "新增失败，请重试。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行。"); return; }
        int id = (Integer) tableModel.getValueAt(row, 0);
        Subscriber s = readForm(id);
        if (s == null) return;
        if (dao.update(s)) {
            JOptionPane.showMessageDialog(this, "修改成功！");
            refreshTable(dao.findAll());
        } else {
            JOptionPane.showMessageDialog(this, "修改失败，请重试。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行。"); return; }
        int id = (Integer) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确认删除该订阅者？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(id)) {
                clearForm();
                refreshTable(dao.findAll());
            } else {
                JOptionPane.showMessageDialog(this, "删除失败。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Subscriber readForm(int id) {
        String name = tfName.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "姓名不能为空。"); return null; }
        return new Subscriber(id, name, tfPhone.getText().trim(),
                tfEmail.getText().trim(), tfAddress.getText().trim());
    }

    private void fillFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (Integer) tableModel.getValueAt(row, 0);
        Subscriber s = dao.findById(id);
        if (s == null) return;
        tfName.setText(s.getName());
        tfPhone.setText(s.getPhone());
        tfEmail.setText(s.getEmail());
        tfAddress.setText(s.getAddress());
    }

    private void clearForm() {
        tfName.setText(""); tfPhone.setText(""); tfEmail.setText(""); tfAddress.setText("");
        table.clearSelection();
    }

    private void refreshTable(List<Subscriber> list) {
        tableModel.setRowCount(0);
        for (Subscriber s : list) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getPhone(), s.getEmail(), s.getAddress()});
        }
    }
}
