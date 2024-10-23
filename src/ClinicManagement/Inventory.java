package ClinicManagement;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;

public class Inventory extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Inventory frame = new Inventory();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Inventory() {
        setTitle("Inventory Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Create table model
        tableModel = new DefaultTableModel(new Object[]{"Item ID", "Item Name", "Quantity", "Unit Price"}, 0);
        table = new JTable(tableModel);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

        // Load data from the database
        loadData();

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> showItemDialog(null));
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Edit Item");
        editButton.addActionListener(e -> editSelectedItem());
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Item");
        deleteButton.addActionListener(e -> deleteSelectedItem());
        buttonPanel.add(deleteButton);

        JButton dashboardButton = new JButton("Dashboard");
        dashboardButton.addActionListener(e -> {
            Main main = new Main();
            main.setVisible(true);
            this.dispose(); // Close the inventory window
        });
        buttonPanel.add(dashboardButton);
    }

    // Load inventory data from the database
    private void loadData() {
        String query = "SELECT * FROM inventory"; // Adjust query as needed
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("item_id"),
                    rs.getString("item_name"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("unit_price")
                });
            }
        } catch (SQLException e) {
            showError("Failed to load data: " + e.getMessage());
        }
    }

    // Show the dialog for adding or editing an inventory item
    private void showItemDialog(Item item) {
        JDialog dialog = new JDialog(this, item == null ? "Add Item" : "Edit Item", true); // Make it modal
        dialog.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Input fields
        JTextField itemNameField = new JTextField(20);
        JFormattedTextField quantityField = new JFormattedTextField(createIntegerFormatter());
        JFormattedTextField unitPriceField = new JFormattedTextField(createDecimalFormatter());

        if (item != null) {
            itemNameField.setText(item.getItemName());
            quantityField.setValue(item.getQuantity());
            unitPriceField.setValue(item.getUnitPrice());
        }

        // Adding components to the dialog
        gbc.gridx = 0; gbc.gridy = 0; dialog.getContentPane().add(new JLabel("Item Name:"), gbc);
        gbc.gridx = 1; dialog.getContentPane().add(itemNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; dialog.getContentPane().add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; dialog.getContentPane().add(quantityField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; dialog.getContentPane().add(new JLabel("Unit Price:"), gbc);
        gbc.gridx = 1; dialog.getContentPane().add(unitPriceField, gbc);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String itemName = itemNameField.getText().trim();
            if (itemName.isEmpty()) {
                showError("Item Name cannot be empty.");
                return;
            }

            Integer quantity = (Integer) quantityField.getValue();
            Double unitPrice = ((Number) unitPriceField.getValue()).doubleValue();

            if (quantity == null || unitPrice == null) {
                showError("Quantity and Unit Price must be valid numbers.");
                return;
            }

            if (item == null) {
                addItem(itemName, quantity, unitPrice);
            } else {
                updateItem(item.getItemId(), itemName, quantity, unitPrice);
            }
            dialog.dispose(); // Close the dialog
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        dialog.getContentPane().add(saveButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true); // Show the dialog
    }

    // Create a NumberFormatter for integer input (quantity)
    private NumberFormatter createIntegerFormatter() {
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false); // Prevent invalid input
        formatter.setCommitsOnValidEdit(true);
        return formatter;
    }

    // Create a NumberFormatter for decimal input (unit price)
    private NumberFormatter createDecimalFormatter() {
        // Create a decimal format allowing for two decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        NumberFormatter formatter = new NumberFormatter(decimalFormat);
        
        // Use Double for value class instead of Integer
        formatter.setValueClass(Double.class);
        
        // Allow for invalid input temporarily while typing
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true); // Commit the value when valid

        // Optional: set a minimum value if required
        formatter.setMinimum(0.0); // Prevent negative values for unit price

        return formatter;
    }


    // Add a new item to the database
    private void addItem(String itemName, int quantity, double unitPrice) {
        String query = "INSERT INTO inventory (item_name, quantity, unit_price) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, itemName);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, unitPrice);
            pstmt.executeUpdate();
            loadData(); // Refresh table data
            showInfo("Item added successfully!");
        } catch (SQLException e) {
            showError("Failed to add item: " + e.getMessage());
        }
    }

    // Update an existing item in the database
    private void updateItem(int itemId, String itemName, int quantity, double unitPrice) {
        String query = "UPDATE inventory SET item_name = ?, quantity = ?, unit_price = ? WHERE item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, itemName);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, unitPrice);
            pstmt.setInt(4, itemId);
            pstmt.executeUpdate();
            loadData(); // Refresh table data
            showInfo("Item updated successfully!");
        } catch (SQLException e) {
            showError("Failed to update item: " + e.getMessage());
        }
    }

    // Delete a selected item from the database
    private void deleteSelectedItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int itemId = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "DELETE FROM inventory WHERE item_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, itemId);
                pstmt.executeUpdate();
                loadData(); // Refresh table data
                showInfo("Item deleted successfully!");
            } catch (SQLException e) {
                showError("Failed to delete item: " + e.getMessage());
            }
        } else {
            showError("No item selected for deletion.");
        }
    }

    // Edit the selected item
    private void editSelectedItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int itemId = (int) tableModel.getValueAt(selectedRow, 0);
            String itemName = (String) tableModel.getValueAt(selectedRow, 1);
            int quantity = (int) tableModel.getValueAt(selectedRow, 2);

            // Correctly retrieve the unit price as a BigDecimal
            BigDecimal unitPrice = (BigDecimal) tableModel.getValueAt(selectedRow, 3);
            showItemDialog(new Item(itemId, itemName, quantity, unitPrice.doubleValue()));
        } else {
            showError("No item selected for editing.");
        }
    }

    // Show error message dialog
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Show info message dialog
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}

// Item class (you might have this already defined)
class Item {
    private int itemId;
    private String itemName;
    private int quantity;
    private double unitPrice;

    public Item(int itemId, String itemName, int quantity, double unitPrice) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
