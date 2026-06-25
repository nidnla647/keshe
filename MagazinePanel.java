package ui;

import dao.MagazineDAO;
import model.Magazine;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MagazinePanel extends JPanel {
    private MagazineDAO dao = new MagazineDAO(); // 杂志数据访问对象 不会直接操作数据库，而是通过DAO来进行间接操作
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfSearch;
    private JTextField tfName, tfCategory, tfPublisher, tfPrice, tfIssn;
    private JTextArea taDesc;

    private static final String[] COLUMNS = {"ID", "名称", "类别", "出版社", "月价(元)", "ISSN"};

    public MagazinePanel() {
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

        tfName      = new JTextField();
        tfCategory  = new JTextField();
        tfPublisher = new JTextField();
        tfPrice     = new JTextField();
        tfIssn      = new JTextField();
        taDesc      = new JTextArea(4, 1);
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);

        addFormRow(p, "名称 *",   tfName);
        addFormRow(p, "类别",     tfCategory);
        addFormRow(p, "出版社",   tfPublisher);
        addFormRow(p, "月价(元)", tfPrice);
        addFormRow(p, "ISSN",     tfIssn);

        p.add(new JLabel("简介"));
        p.add(Box.createVerticalStrut(4));
        p.add(new JScrollPane(taDesc));
        p.add(Box.createVerticalStrut(12));

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

    private void addFormRow(JPanel p, String label, JComponent field) {
        p.add(new JLabel(label));
        p.add(Box.createVerticalStrut(3));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        p.add(field);
        p.add(Box.createVerticalStrut(8));
    }

    private void doInsert() {
        Magazine m = readForm(-1);
        if (m == null) return;
        if (dao.insert(m)) {
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
        Magazine m = readForm(id);
        if (m == null) return;
        if (dao.update(m)) {
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
        int confirm = JOptionPane.showConfirmDialog(this, "确认删除该杂志？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(id)) {
                clearForm();
                refreshTable(dao.findAll());
            } else {
                JOptionPane.showMessageDialog(this, "删除失败（可能存在关联订阅记录）。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Magazine readForm(int id) {
        String name = tfName.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "名称不能为空。"); return null; }
        double price = 0;
        try { price = Double.parseDouble(tfPrice.getText().trim()); } catch (NumberFormatException ignored) {}
        return new Magazine(id, name, tfCategory.getText().trim(),
                tfPublisher.getText().trim(), price,
                tfIssn.getText().trim(), taDesc.getText().trim());
    }

    private void fillFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (Integer) tableModel.getValueAt(row, 0);
        Magazine m = dao.findById(id);
        if (m == null) return;
        tfName.setText(m.getName());
        tfCategory.setText(m.getCategory());
        tfPublisher.setText(m.getPublisher());
        tfPrice.setText(String.valueOf(m.getPrice()));
        tfIssn.setText(m.getIssn());
        taDesc.setText(m.getDescription());
    }

    private void clearForm() {
        tfName.setText(""); tfCategory.setText(""); tfPublisher.setText("");
        tfPrice.setText(""); tfIssn.setText(""); taDesc.setText("");
        table.clearSelection();
    }

    private void refreshTable(List<Magazine> list) {
        tableModel.setRowCount(0);
        for (Magazine m : list) {
            tableModel.addRow(new Object[]{
                    m.getId(), m.getName(), m.getCategory(),
                    m.getPublisher(), m.getPrice(), m.getIssn()
            });
        }
    }
}
