package ClinicManagement;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Treatments extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtTreatmentType;
    private JSpinner dateSpinner; // Spinner for date selection
    private JTextArea txtNotes; // Text area for notes
    private JTable tableTreatments;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbPatientName; // Dropdown for patient names
    private JButton btnSave, btnUpdate, btnDelete;
    
    private Connection connection; // Database connection

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        // Top panel for input fields
        JPanel panelInputs = new JPanel();
        contentPane.add(panelInputs, BorderLayout.NORTH);
        panelInputs.setLayout(new GridLayout(4, 2));

        panelInputs.add(new JLabel("Patient Name:"));
        cbPatientName = new JComboBox<>(loadPatientNames()); // Load patient names from the database
        panelInputs.add(cbPatientName);

        panelInputs.add(new JLabel("Treatment Type:"));
        txtTreatmentType = new JTextField();
        panelInputs.add(txtTreatmentType);

        panelInputs.add(new JLabel("Treatment Date:"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        dateSpinner.setValue(new Date()); // Set current date
        panelInputs.add(dateSpinner);

        panelInputs.add(new JLabel("Notes:"));
        txtNotes = new JTextArea(3, 20);
        panelInputs.add(new JScrollPane(txtNotes));

        // Buttons for CRUD operations
        btnSave = new JButton("Save");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        JPanel panelButtons = new JPanel();
        panelButtons.add(btnSave);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        contentPane.add(panelButtons, BorderLayout.SOUTH);

        // Table for displaying treatments
        tableModel = new DefaultTableModel(new String[]{"ID", "Patient Name", "Treatment Type", "Treatment Date", "Notes"}, 0);
        tableTreatments = new JTable(tableModel);
        contentPane.add(new JScrollPane(tableTreatments), BorderLayout.CENTER);

        btnSave.addActionListener(e -> saveTreatment());
        btnUpdate.addActionListener(e -> updateTreatment());
        btnDelete.addActionListener(e -> deleteTreatment());

        loadTreatments(); // Load treatments when the frame is initialized
    }

    private String[] loadPatientNames() {
        ArrayList<String> patientNames = new ArrayList<>();
        // Load patient names from the database
        try {
            connection = DatabaseConnection.getConnection(); // Implement this method to establish DB connection
            String sql = "SELECT CONCAT(first_name, ' ', last_name) AS full_name FROM users WHERE user_type = 'patient'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                patientNames.add(rs.getString("full_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading patient names: " + e.getMessage());
        }
        return patientNames.toArray(new String[0]);
    }

    private void loadTreatments() {
        try {
            tableModel.setRowCount(0); // Clear existing rows
            String sql = "SELECT t.id, CONCAT(u.first_name, ' ', u.last_name) AS patient_name, t.treatment_type, t.treatment_date, t.notes FROM treatments t JOIN users u ON t.user_id = u.id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("treatment_type"),
                        rs.getDate("treatment_date"),
                        rs.getString("notes")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading treatments: " + e.getMessage());
        }
    }

    private void saveTreatment() {
        String treatmentType = txtTreatmentType.getText().trim();
        Date treatmentDate = (Date) dateSpinner.getValue();
        String notes = txtNotes.getText().trim();
        String patientName = (String) cbPatientName.getSelectedItem();
        
        // Assuming you have a method to get user_id based on patient name
        Integer patientId = getUserIdByName(patientName);

        if (treatmentType.isEmpty() || notes.isEmpty()) {
            showWarning("Please fill in all fields.");
            return;
        }

        try {
            String sql = "INSERT INTO treatments (user_id, treatment_type, treatment_date, notes) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, patientId);
            pstmt.setString(2, treatmentType);
            pstmt.setDate(3, new java.sql.Date(treatmentDate.getTime()));
            pstmt.setString(4, notes);
            pstmt.executeUpdate();
            loadTreatments(); // Refresh table
            showInfo("Treatment saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error saving treatment: " + e.getMessage());
        }
    }

    private Integer getUserIdByName(String fullName) {
        Integer userId = null;
        try {
            String sql = "SELECT id FROM users WHERE CONCAT(first_name, ' ', last_name) = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, fullName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    private void updateTreatment() {
        int selectedRow = tableTreatments.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a treatment to update.");
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String treatmentType = txtTreatmentType.getText().trim();
        Date treatmentDate = (Date) dateSpinner.getValue();
        String notes = txtNotes.getText().trim();
        String patientName = (String) cbPatientName.getSelectedItem();
        Integer patientId = getUserIdByName(patientName);

        if (treatmentType.isEmpty() || notes.isEmpty()) {
            showWarning("Please fill in all fields.");
            return;
        }

        try {
            String sql = "UPDATE treatments SET user_id = ?, treatment_type = ?, treatment_date = ?, notes = ? WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, patientId);
            pstmt.setString(2, treatmentType);
            pstmt.setDate(3, new java.sql.Date(treatmentDate.getTime()));
            pstmt.setString(4, notes);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            loadTreatments(); // Refresh table
            showInfo("Treatment updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error updating treatment: " + e.getMessage());
        }
    }

    private void deleteTreatment() {
        int selectedRow = tableTreatments.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a treatment to delete.");
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        try {
            String sql = "DELETE FROM treatments WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            loadTreatments(); // Refresh table
            showInfo("Treatment deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error deleting treatment: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
