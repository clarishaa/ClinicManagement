package ClinicManagement;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import com.toedter.calendar.JDateChooser; // Import JDateChooser for date picking

public class Treatments extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<String> cbTreatmentType; // Dropdown for treatment type
    private JTextField txtNotes; // For the notes field
    private JTable tableTreatments;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbPatient; // Dropdown for patient names
    private HashMap<String, Integer> patientIdMap; // Map to store names and their corresponding IDs
    private JButton btnSave, btnUpdate, btnDelete;
    private JDateChooser dateChooser; // Calendar picker for treatment date

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

        panelInputs.add(new JLabel("Patient:"));
        cbPatient = new JComboBox<>(loadPatientNames()); // Load patient names from the database
        panelInputs.add(cbPatient);

        panelInputs.add(new JLabel("Treatment Type:")); // Changed label to treatment type
        cbTreatmentType = new JComboBox<>(new String[]{"Type 1", "Type 2", "Type 3"}); // Example treatment types
        panelInputs.add(cbTreatmentType);

        panelInputs.add(new JLabel("Treatment Date:")); // Add treatment date
        dateChooser = new JDateChooser(); // Calendar picker
        panelInputs.add(dateChooser);

        panelInputs.add(new JLabel("Notes:")); // For notes input
        txtNotes = new JTextField();
        panelInputs.add(txtNotes);

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
        tableModel = new DefaultTableModel(new String[]{"ID", "Patient", "Treatment Type", "Treatment Date", "Notes"}, 0);
        tableTreatments = new JTable(tableModel);
        contentPane.add(new JScrollPane(tableTreatments), BorderLayout.CENTER);

        btnSave.addActionListener(e -> saveTreatment());
        btnUpdate.addActionListener(e -> updateTreatment());
        btnDelete.addActionListener(e -> deleteTreatment());

        loadTreatments(); // Load treatments when the frame is initialized
    }

    private String[] loadPatientNames() {
        patientIdMap = new HashMap<>(); // Initialize map to store names and IDs
        ArrayList<String> patientNames = new ArrayList<>();
        // Load patient names and IDs from the database
        try {
            connection = DatabaseConnection.getConnection(); // Implement this method to establish DB connection
            String sql = "SELECT id, first_name, last_name FROM users WHERE user_type = 'patient'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                patientNames.add(fullName);
                patientIdMap.put(fullName, id); // Store mapping of name to ID
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
            String sql = "SELECT t.id, u.first_name, u.last_name, t.treatment_type, t.treatment_date, t.notes " +
                         "FROM treatments t JOIN users u ON t.user_id = u.id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("treatment_type"),
                        rs.getDate("treatment_date"), // Change to Date type for correct display
                        rs.getString("notes")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading treatments: " + e.getMessage());
        }
    }

    private void saveTreatment() {
        String treatmentType = (String) cbTreatmentType.getSelectedItem(); // Get selected treatment type
        java.util.Date selectedDate = dateChooser.getDate(); // Get selected date from the calendar picker
        String treatmentDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate); // Format date to String
        String notes = txtNotes.getText().trim();
        String selectedPatient = (String) cbPatient.getSelectedItem();
        Integer patientId = patientIdMap.get(selectedPatient); // Get ID from the map

        if (treatmentType.isEmpty() || treatmentDate.isEmpty() || notes.isEmpty() || selectedPatient == null) {
            showWarning("Please fill in all fields.");
            return;
        }

        try {
            String sql = "INSERT INTO treatments (user_id, treatment_type, treatment_date, notes) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, patientId);
            pstmt.setString(2, treatmentType);
            pstmt.setDate(3, Date.valueOf(treatmentDate)); // Convert String to Date
            pstmt.setString(4, notes);
            pstmt.executeUpdate();
            loadTreatments(); // Refresh table
            showInfo("Treatment saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error saving treatment: " + e.getMessage());
        }
    }

    private void updateTreatment() {
        int selectedRow = tableTreatments.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a treatment to update.");
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String treatmentType = (String) cbTreatmentType.getSelectedItem(); // Get selected treatment type
        java.util.Date selectedDate = dateChooser.getDate(); // Get selected date from the calendar picker
        String treatmentDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate); // Format date to String
        String notes = txtNotes.getText().trim();
        String selectedPatient = (String) cbPatient.getSelectedItem();
        Integer patientId = patientIdMap.get(selectedPatient); // Get ID from the map

        if (treatmentType.isEmpty() || treatmentDate.isEmpty() || notes.isEmpty() || selectedPatient == null) {
            showWarning("Please fill in all fields.");
            return;
        }

        try {
            String sql = "UPDATE treatments SET user_id = ?, treatment_type = ?, treatment_date = ?, notes = ? WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, patientId);
            pstmt.setString(2, treatmentType);
            pstmt.setDate(3, Date.valueOf(treatmentDate)); // Convert String to Date
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
