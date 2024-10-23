package ClinicManagement;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;

public class Treatments extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable treatmentTable;
    private DefaultTableModel treatmentTableModel;

    // Define treatment types and their corresponding fixed billing amounts
    private static final String[][] TREATMENT_TYPES = {
        {"Consultation", "100"},
        {"Filling", "300"},
        {"Cleaning", "200"},
        {"Extraction", "400"},
        // Add more treatment types as needed
    };

    // Dropdown for users
    private JComboBox<String> userDropdown;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Treatments frame = new Treatments();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Treatments() {
        setTitle("Treatment Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Treatment Table with Billing Info
        treatmentTableModel = new DefaultTableModel(new Object[]{"ID", "User ID", "Treatment Type", "Treatment Date", "Notes", "Amount", "Status"}, 0);
        treatmentTable = new JTable(treatmentTableModel);
        contentPane.add(new JScrollPane(treatmentTable), BorderLayout.CENTER);

        loadTreatmentData();

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        JButton addButton = createButton("Add Treatment", e -> showTreatmentDialog(null));
        buttonPanel.add(addButton);

        JButton editButton = createButton("Edit Treatment", e -> editSelectedTreatment());
        buttonPanel.add(editButton);

        JButton deleteButton = createButton("Delete Treatment", e -> deleteSelectedTreatment());
        buttonPanel.add(deleteButton);
        
        // Load user dropdown
        loadUserDropdown();
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }

    private void loadTreatmentData() {
        String query = "SELECT t.id, t.user_id, t.treatment_type, t.treatment_date, t.notes, " +
                       "b.amount, b.status " +
                       "FROM treatments t LEFT JOIN billing b ON t.id = b.treatment_id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            treatmentTableModel.setRowCount(0);
            while (rs.next()) {
                treatmentTableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("treatment_type"),
                    rs.getDate("treatment_date"),
                    rs.getString("notes"),
                    rs.getInt("amount"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            showError("Failed to load treatment data: " + e.getMessage());
        }
    }

    private void loadUserDropdown() {
        userDropdown = new JComboBox<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, first_name, last_name FROM users WHERE user_type = 'patient'")) {

            while (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                userDropdown.addItem(rs.getInt("id") + " - " + fullName);
            }
        } catch (SQLException e) {
            showError("Failed to load users: " + e.getMessage());
        }
    }

    private void showTreatmentDialog(Treatment treatment) {
        JDialog dialog = new JDialog(this, treatment == null ? "Add Treatment" : "Edit Treatment", true);
        dialog.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

        JComboBox<String> treatmentTypeDropdown = new JComboBox<>();
        for (String[] treatmentType : TREATMENT_TYPES) {
            treatmentTypeDropdown.addItem(treatmentType[0]);
        }

        JSpinner treatmentDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(treatmentDateSpinner, "yyyy-MM-dd");
        treatmentDateSpinner.setEditor(dateEditor);
        
        JTextArea notesField = new JTextArea(3, 20);
        notesField.setLineWrap(true);
        notesField.setWrapStyleWord(true);

        // Billing Info
        JLabel amountLabel = new JLabel("Amount: 0"); // Initialize with a default value
        JLabel statusLabel = new JLabel();

        // Populate fields if editing an existing treatment
        if (treatment != null) {
            String fullName = getFullName(treatment.getUserId());
            userDropdown.setSelectedItem(treatment.getUserId() + " - " + fullName);
            treatmentTypeDropdown.setSelectedItem(treatment.getTreatmentType());
            treatmentDateSpinner.setValue(treatment.getTreatmentDate());
            notesField.setText(treatment.getNotes());

            // Retrieve billing information
            amountLabel.setText("Amount: " + getBillingAmount(treatment.getId()));
            statusLabel.setText("Status: " + getBillingStatus(treatment.getId()));
        }

        // Add listeners to update the amount automatically
        treatmentTypeDropdown.addActionListener(e -> updateAmountLabel(amountLabel, treatmentTypeDropdown, treatmentDateSpinner, treatment));
        ((JSpinner.DefaultEditor) treatmentDateSpinner.getEditor()).getTextField().addActionListener(e -> updateAmountLabel(amountLabel, treatmentTypeDropdown, treatmentDateSpinner, treatment));

        // Add components to dialog
        gbc.gridx = 0; gbc.gridy = 0; dialog.getContentPane().add(new JLabel("User:"), gbc);
        gbc.gridx = 1; dialog.getContentPane().add(userDropdown, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; dialog.getContentPane().add(new JLabel("Treatment Type:"), gbc);
        gbc.gridx = 1; dialog.getContentPane().add(treatmentTypeDropdown, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; dialog.getContentPane().add(new JLabel("Treatment Date:"), gbc);
        gbc.gridx = 1; dialog.getContentPane().add(treatmentDateSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; dialog.getContentPane().add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; dialog.getContentPane().add(new JScrollPane(notesField), gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; dialog.getContentPane().add(amountLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 5; dialog.getContentPane().add(statusLabel, gbc);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String selectedUser = (String) userDropdown.getSelectedItem();
            String selectedTreatmentType = (String) treatmentTypeDropdown.getSelectedItem();
            String treatmentDate = new SimpleDateFormat("yyyy-MM-dd").format(treatmentDateSpinner.getValue());
            String notes = notesField.getText().trim();

            if (selectedUser == null || selectedTreatmentType == null || treatmentDate.isEmpty() || notes.isEmpty()) {
                showError("All fields are required! Please fill in all fields.");
                return; // Exit the method if validation fails
            }

            int userId = Integer.parseInt(selectedUser.split(" - ")[0]);

            if (treatment == null) {
                addTreatment(userId, selectedTreatmentType, treatmentDate, notes);
            } else {
                updateTreatment(treatment.getId(), userId, selectedTreatmentType, treatmentDate, notes);
            }
            dialog.dispose();
        });

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; // Center the button
        dialog.getContentPane().add(saveButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Method to update the amount label based on the selected treatment type and date
    private void updateAmountLabel(JLabel amountLabel, JComboBox<String> treatmentTypeDropdown, JSpinner treatmentDateSpinner, Treatment treatment) {
        // Assuming getBillingAmount can take treatment type and date
        String selectedTreatmentType = (String) treatmentTypeDropdown.getSelectedItem();
        String treatmentDate = new SimpleDateFormat("yyyy-MM-dd").format(treatmentDateSpinner.getValue());
        
        // Update amount label
        int amount = getBillingAmount(selectedTreatmentType, treatmentDate); // Implement this method accordingly
        amountLabel.setText("Amount: " + amount);
    }

    private int getBillingAmount(String selectedTreatmentType, String treatmentDate) {
        // Iterate over treatment types to find the corresponding amount
        for (String[] treatment : TREATMENT_TYPES) {
            if (treatment[0].equals(selectedTreatmentType)) {
                return Integer.parseInt(treatment[1]); // Return the corresponding amount
            }
        }
        return 0; // Return 0 if the treatment type is not found
    }


	private String getFullName(int userId) {
        String fullName = null;
        String query = "SELECT first_name, last_name FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                }
            }
        } catch (SQLException e) {
            showError("Failed to retrieve user name: " + e.getMessage());
        }
        return fullName;
    }

    private void addTreatment(int userId, String treatmentType, String treatmentDate, String notes) {
        String query = "INSERT INTO treatments (user_id, treatment_type, treatment_date, notes) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, treatmentType);
            pstmt.setDate(3, Date.valueOf(treatmentDate));
            pstmt.setString(4, notes);
            pstmt.executeUpdate();

            // Get the generated treatment ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int treatmentId = generatedKeys.getInt(1);
                    addBilling(treatmentId, treatmentType);
                }
            }
            loadTreatmentData(); // Refresh the table
        } catch (SQLException e) {
            showError("Failed to add treatment: " + e.getMessage());
        }
    }

    private void addBilling(int treatmentId, String treatmentType) {
        for (String[] treatment : TREATMENT_TYPES) {
            if (treatment[0].equals(treatmentType)) {
                String query = "INSERT INTO billing (treatment_id, amount, status) VALUES (?, ?, 'Pending')";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, treatmentId);
                    pstmt.setInt(2, Integer.parseInt(treatment[1]));
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    showError("Failed to add billing: " + e.getMessage());
                }
                break;
            }
        }
    }

    private void updateTreatment(int treatmentId, int userId, String treatmentType, String treatmentDate, String notes) {
        String query = "UPDATE treatments SET user_id = ?, treatment_type = ?, treatment_date = ?, notes = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, treatmentType);
            pstmt.setDate(3, Date.valueOf(treatmentDate));
            pstmt.setString(4, notes);
            pstmt.setInt(5, treatmentId);
            pstmt.executeUpdate();

            // Update billing if necessary
            updateBilling(treatmentId, treatmentType);
            loadTreatmentData(); // Refresh the table
        } catch (SQLException e) {
            showError("Failed to update treatment: " + e.getMessage());
        }
    }

    private void updateBilling(int treatmentId, String treatmentType) {
        for (String[] treatment : TREATMENT_TYPES) {
            if (treatment[0].equals(treatmentType)) {
                String query = "UPDATE billing SET amount = ? WHERE treatment_id = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, Integer.parseInt(treatment[1]));
                    pstmt.setInt(2, treatmentId);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    showError("Failed to update billing: " + e.getMessage());
                }
                break;
            }
        }
    }

    private void editSelectedTreatment() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow != -1) {
            int treatmentId = (int) treatmentTableModel.getValueAt(selectedRow, 0);
            int userId = (int) treatmentTableModel.getValueAt(selectedRow, 1);
            String treatmentType = (String) treatmentTableModel.getValueAt(selectedRow, 2);
            Date treatmentDate = (Date) treatmentTableModel.getValueAt(selectedRow, 3);
            String notes = (String) treatmentTableModel.getValueAt(selectedRow, 4);

            Treatment treatment = new Treatment(treatmentId, userId, treatmentType, treatmentDate, notes);
            showTreatmentDialog(treatment);
        } else {
            showError("Please select a treatment to edit.");
        }
    }

    private void deleteSelectedTreatment() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow != -1) {
            int treatmentId = (int) treatmentTableModel.getValueAt(selectedRow, 0);
            String query = "DELETE FROM treatments WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, treatmentId);
                pstmt.executeUpdate();
                loadTreatmentData(); // Refresh the table
            } catch (SQLException e) {
                showError("Failed to delete treatment: " + e.getMessage());
            }
        } else {
            showError("Please select a treatment to delete.");
        }
    }

    private String getBillingAmount(int treatmentId) {
        String amount = "0";
        String query = "SELECT amount FROM billing WHERE treatment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, treatmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    amount = String.valueOf(rs.getInt("amount"));
                }
            }
        } catch (SQLException e) {
            showError("Failed to retrieve billing amount: " + e.getMessage());
        }
        return amount;
    }

    private String getBillingStatus(int treatmentId) {
        String status = "N/A";
        String query = "SELECT status FROM billing WHERE treatment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, treatmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    status = rs.getString("status");
                }
            }
        } catch (SQLException e) {
            showError("Failed to retrieve billing status: " + e.getMessage());
        }
        return status;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

class Treatment {
    private int id;
    private int userId;
    private String treatmentType;
    private Date treatmentDate;
    private String notes;

    public Treatment(int id, int userId, String treatmentType, Date treatmentDate, String notes) {
        this.id = id;
        this.userId = userId;
        this.treatmentType = treatmentType;
        this.treatmentDate = treatmentDate;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public Date getTreatmentDate() {
        return treatmentDate;
    }

    public String getNotes() {
        return notes;
    }
}
