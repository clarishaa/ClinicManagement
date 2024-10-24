package ClinicManagement;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;

public class Patients extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Patients frame = new Patients();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Patients() {
        setTitle("Patient Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        tableModel = new DefaultTableModel(new Object[]{"ID", "First Name", "Last Name", "Gender", "Birthdate"}, 0);
        table = new JTable(tableModel);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        JButton addButton = new JButton("Add Patient");
       
        addButton.addActionListener(e -> showPatientDialog(null));
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Edit Patient");
        editButton.addActionListener(e -> editSelectedPatient());
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Patient");
        deleteButton.addActionListener(e -> deleteSelectedPatient());
        buttonPanel.add(deleteButton);
        
        JButton deleteButton_1 = new JButton("Dashboard");
        deleteButton_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Main main = new Main();
        		main .setVisible(true);

        	}
        });
        buttonPanel.add(deleteButton_1);
    }

    private void loadData() {
        String query = "SELECT * FROM users WHERE user_type = 'patient'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("gender"),
                    rs.getDate("birthdate")
                });
            }
        } catch (SQLException e) {
            showError("Failed to load data: " + e.getMessage());
        }
    }

    private void showPatientDialog(Patient patient) {
        JDialog dialog = new JDialog(this, patient == null ? "Add Patient" : "Edit Patient", true);
        dialog.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();

        String[] genders = {"Male", "Female", "Other"};
        JComboBox<String> genderDropdown = new JComboBox<>(genders);

        JSpinner birthdateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(birthdateSpinner, "yyyy-MM-dd");
        birthdateSpinner.setEditor(dateEditor);

        if (patient != null) {
            firstNameField.setText(patient.getFirstName());
            lastNameField.setText(patient.getLastName());
            genderDropdown.setSelectedItem(patient.getGender());
            birthdateSpinner.setValue(patient.getBirthdate());
        }

        gbc.gridx = 0; gbc.gridy = 0; dialog.getContentPane().add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; dialog.getContentPane().add(firstNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; dialog.getContentPane().add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; dialog.getContentPane().add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; dialog.getContentPane().add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; dialog.getContentPane().add(genderDropdown, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; dialog.getContentPane().add(new JLabel("Birthdate:"), gbc);
        gbc.gridx = 1; dialog.getContentPane().add(birthdateSpinner, gbc);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String selectedGender = (String) genderDropdown.getSelectedItem();
            String birthdate = new SimpleDateFormat("yyyy-MM-dd").format(birthdateSpinner.getValue());

            if (firstName.isEmpty() || lastName.isEmpty() || selectedGender == null || birthdate.isEmpty()) {
                showError("All fields are required! Please fill in all fields.");
                return;
            }

            if (patient == null) {
                addPatient(firstName, lastName, selectedGender, birthdate);
            } else {
                updatePatient(patient.getId(), firstName, lastName, selectedGender, birthdate);
            }
            dialog.dispose();
        });

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        dialog.getContentPane().add(saveButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addPatient(String firstName, String lastName, String gender, String birthdate) {
        String query = "INSERT INTO users (first_name, last_name, gender, birthdate, user_type) VALUES (?, ?, ?, ?, 'patient')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, gender);
            pstmt.setDate(4, java.sql.Date.valueOf(birthdate));
            pstmt.executeUpdate();
            loadData();
            showInfo("Patient added successfully!");
        } catch (SQLException e) {
            showError("Failed to add patient: " + e.getMessage());
        }
    }

    private void updatePatient(int id, String firstName, String lastName, String gender, String birthdate) {
        String query = "UPDATE users SET first_name = ?, last_name = ?, gender = ?, birthdate = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, gender);
            pstmt.setDate(4, java.sql.Date.valueOf(birthdate));
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            loadData();
            showInfo("Patient updated successfully!");
        } catch (SQLException e) {
            showError("Failed to update patient: " + e.getMessage());
        }
    }

    private void deleteSelectedPatient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "DELETE FROM users WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                loadData();
                showInfo("Patient deleted successfully!");
            } catch (SQLException e) {
                showError("Failed to delete patient: " + e.getMessage());
            }
        } else {
            showError("No patient selected for deletion.");
        }
    }

    private void editSelectedPatient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String firstName = (String) tableModel.getValueAt(selectedRow, 1);
            String lastName = (String) tableModel.getValueAt(selectedRow, 2);
            String gender = (String) tableModel.getValueAt(selectedRow, 3);
            Date birthdate = (Date) tableModel.getValueAt(selectedRow, 4);
            showPatientDialog(new Patient(id, firstName, lastName, gender, birthdate));
        } else {
            showError("No patient selected for editing.");
        }
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private class Patient {
        private int id;
        private String firstName;
        private String lastName;
        private String gender;
        private Date birthdate;

        public Patient(int id, String firstName, String lastName, String gender, Date birthdate) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.gender = gender;
            this.birthdate = birthdate;
        }

        public int getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getGender() {
            return gender;
        }

        public Date getBirthdate() {
            return birthdate;
        }
    }
}
