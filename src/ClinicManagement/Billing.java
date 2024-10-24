package ClinicManagement;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Billing extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable billingTable;
    private DefaultTableModel billingTableModel;
    private Map<Integer, String> treatmentMap = new HashMap<>();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Billing frame = new Billing();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public Billing() {
        setTitle("Billing Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        setLocationRelativeTo(null);

        billingTableModel = new DefaultTableModel(new Object[]{"ID", "Treatment Type", "Amount", "Status"}, 0);
        billingTable = new JTable(billingTableModel);
        JScrollPane scrollPane = new JScrollPane(billingTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        loadTreatmentTypes();
        loadBillingData();

        JPanel buttonPanel = new JPanel();
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        JButton addButton = new JButton("Add Billing");
        addButton.addActionListener(e -> showBillingForm(null));
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Edit Billing");
        editButton.addActionListener(e -> editSelectedBilling());
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Billing");
        deleteButton.addActionListener(e -> deleteSelectedBilling());
        buttonPanel.add(deleteButton);
    }

    private void loadTreatmentTypes() {
        String query = "SELECT id, treatment_type FROM treatments";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            treatmentMap.clear();
            while (rs.next()) {
                treatmentMap.put(rs.getInt("id"), rs.getString("treatment_type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBillingData() {
        String query = "SELECT * FROM billing";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            billingTableModel.setRowCount(0);
            while (rs.next()) {
                billingTableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    treatmentMap.get(rs.getInt("treatment_id")),
                    "₱" + rs.getInt("amount"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void showBillingForm(BillingRecord billing) {
        JDialog dialog = new JDialog(this, billing == null ? "Add Billing" : "Edit Billing", true);
        dialog.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel treatmentLabel = new JLabel("Treatment Type:");
        JLabel treatmentDisplay = new JLabel(); 
        JComboBox<String> treatmentDropdown = new JComboBox<>();
        for (String treatmentType : treatmentMap.values()) {
            treatmentDropdown.addItem(treatmentType);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(treatmentLabel, gbc);
        gbc.gridx = 1;
        if (billing != null) {
            treatmentDisplay.setText(treatmentMap.get(billing.getTreatmentId()));
            dialog.add(treatmentDisplay, gbc);
        } else {
            dialog.add(treatmentDropdown, gbc);
        }

        JLabel amountLabel = new JLabel("Amount:");
        JFormattedTextField amountField = new JFormattedTextField(java.text.NumberFormat.getIntegerInstance());
        amountField.setColumns(5);
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(amountLabel, gbc);
        gbc.gridx = 1;
        dialog.add(amountField, gbc);

        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusDropdown = new JComboBox<>(new String[] {"Pending", "Paid", "Cancelled"});
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(statusLabel, gbc);
        gbc.gridx = 1;
        dialog.add(statusDropdown, gbc);

        if (billing != null) {
            amountField.setValue(billing.getAmount());
            statusDropdown.setSelectedItem(billing.getStatus());
        }

        JButton saveButton = new JButton("Save");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        saveButton.setPreferredSize(new java.awt.Dimension(150, 30));
        saveButton.addActionListener(e -> {
            int treatmentId = billing == null 
                ? getKeyByValue(treatmentMap, (String) treatmentDropdown.getSelectedItem())
                : billing.getTreatmentId();

            int amount = ((Number) amountField.getValue()).intValue();
            String status = (String) statusDropdown.getSelectedItem();

            if (billing == null) {
                addBillingRecord(treatmentId, amount, status);
            } else {
                updateBillingRecord(billing.getId(), treatmentId, amount, status);
            }
            dialog.dispose();
            loadBillingData();
        });
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        dialog.getContentPane().add(saveButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }



    private <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void addBillingRecord(int treatmentId, int amount, String status) {
        String query = "INSERT INTO billing (treatment_id, amount, status) VALUES (" + treatmentId + ", " + amount + ", '" + status + "')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBillingRecord(int billingId, int treatmentId, int amount, String status) {
        String query = "UPDATE billing SET treatment_id = " + treatmentId + ", amount = " + amount + ", status = '" + status + "' WHERE id = " + billingId;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editSelectedBilling() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow != -1) {
            int billingId = (int) billingTableModel.getValueAt(selectedRow, 0);
            String selectedTreatment = (String) billingTableModel.getValueAt(selectedRow, 1);
            int treatmentId = getKeyByValue(treatmentMap, selectedTreatment);
            int amount = (int) billingTableModel.getValueAt(selectedRow, 2);
            String status = (String) billingTableModel.getValueAt(selectedRow, 3);

            BillingRecord billing = new BillingRecord(billingId, treatmentId, amount, status);
            showBillingForm(billing);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a billing record to edit.");
        }
    }

    private void deleteSelectedBilling() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow != -1) {
            int billingId = (int) billingTableModel.getValueAt(selectedRow, 0);
            String query = "DELETE FROM billing WHERE id = " + billingId;
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.executeUpdate(query);
                loadBillingData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a billing record to delete.");
        }
    }
}

class BillingRecord {
    private int id;
    private int treatmentId;
    private int amount;
    private String status;

    public BillingRecord(int id, int treatmentId, int amount, String status) {
        this.id = id;
        this.treatmentId = treatmentId;
        this.amount = amount;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public int getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}
