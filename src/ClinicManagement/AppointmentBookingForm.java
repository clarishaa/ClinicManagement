package ClinicManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppointmentBookingForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField patientNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> serviceComboBox;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private JButton bookButton;
    private JButton cancelButton;
    private Connection connection;
    private int appointmentId = -1;
    
    private static AppointmentManagementSystem parentSystem;

    public AppointmentBookingForm(int appointmentId, AppointmentManagementSystem parentSystem) {
        this.appointmentId = appointmentId;
        this.parentSystem = parentSystem;
        setTitle(appointmentId == -1 ? "Book Appointment - CleanSmile Dental Clinic" : "Edit Appointment - CleanSmile Dental Clinic");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 400, 600, 20, 20));
        setBackground(new Color(0, 0, 0, 0));

        // Initialize database connection
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }

        // Main panel
        JScrollPane mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Close button
        JButton closeButton = createCloseButton();
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.setOpaque(false);
        closePanel.add(closeButton);
        add(closePanel, BorderLayout.NORTH);

        // Action Listeners
        bookButton.addActionListener(this::bookAppointment);
        cancelButton.addActionListener(e -> dispose());

        // Make the window draggable
        makeDraggable();

        if (appointmentId != -1) {
            loadAppointmentData();
            bookButton.setText("Update Appointment");
        }
    }

    private JScrollPane createMainPanel() {
        JPanel mainPanel = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(240, 248, 255));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setOpaque(false);

        // Add components to the main panel
        addComponentsToMainPanel(mainPanel);

        // Wrap the main panel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        return scrollPane;
    }

    private void addComponentsToMainPanel(JPanel mainPanel) {
        // Logo
        addLogo(mainPanel);

        // Form fields
        addFormField(mainPanel, "Patient Name:", patientNameField = new JTextField(20));
        addFormField(mainPanel, "Email:", emailField = new JTextField(20));
        addFormField(mainPanel, "Phone:", phoneField = new JTextField(20));

        // Service
        String[] services = {"General Checkup", "Teeth Cleaning", "Filling", "Root Canal", "Orthodontics"};
        serviceComboBox = new JComboBox<>(services);
        addFormField(mainPanel, "Service:", serviceComboBox);

        // Date
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(dateEditor);
        addFormField(mainPanel, "Date:", dateSpinner);

        // Time
        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        addFormField(mainPanel, "Time:", timeSpinner);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        bookButton = createStyledButton("Book Appointment", new Color(0, 153, 153));
        cancelButton = createStyledButton("Cancel", new Color(220, 20, 60));

        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);
    }

    private void addLogo(JPanel panel) {
        ImageIcon logoIcon = new ImageIcon("C:\\Users\\adley\\OneDrive\\Pictures\\Screenshots\\LogoClinic.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logoLabel);
        panel.add(Box.createVerticalStrut(20));
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));

        field.setMaximumSize(new Dimension(300, 30));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (field instanceof JTextField) {
            ((JTextField) field).setFont(new Font("Arial", Font.PLAIN, 14));
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 153, 153), 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        }
        panel.add(field);
        panel.add(Box.createVerticalStrut(15));
    }

    private JButton createCloseButton() {
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setForeground(Color.BLACK);
        closeButton.setBackground(new Color(0, 153, 153));
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());
        return closeButton;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }

    private void makeDraggable() {
        MouseAdapter ma = new MouseAdapter() {
            private int pressedX;
            private int pressedY;

            @Override
            public void mousePressed(MouseEvent e) {
                pressedX = e.getX();
                pressedY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - pressedX, getY() + e.getY() - pressedY);
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private void loadAppointmentData() {
        String query = "SELECT * FROM appointments WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                patientNameField.setText(rs.getString("patient_name"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone"));
                serviceComboBox.setSelectedItem(rs.getString("service"));
                dateSpinner.setValue(rs.getDate("appointment_date"));
                timeSpinner.setValue(rs.getTime("appointment_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading appointment data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookAppointment(ActionEvent e) {
        String patientName = patientNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String service = (String) serviceComboBox.getSelectedItem();
        Date date = (Date) dateSpinner.getValue();
        Date time = (Date) timeSpinner.getValue();

        int affectedRows = 0;
		if (affectedRows > 0) {
            String message = (appointmentId == -1) ? "Appointment booked successfully!" : "Appointment updated successfully!";
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        // ... (rest of the method remains the same)
    


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = dateFormat.format(date);
        String formattedTime = timeFormat.format(time);

        String query;
        if (appointmentId == -1) {
            query = "INSERT INTO appointments (patient_name, email, phone, service, appointment_date, appointment_time) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            query = "UPDATE appointments SET patient_name = ?, email = ?, phone = ?, service = ?, appointment_date = ?, appointment_time = ? WHERE id = ?";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, patientName);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, service);
            pstmt.setString(5, formattedDate);
            pstmt.setString(6, formattedTime);
            if (appointmentId != -1) {
                pstmt.setInt(7, appointmentId);
            }

            int affectedRows1 = pstmt.executeUpdate();
            if (affectedRows1 > 0) {
                String message = (appointmentId == -1) ? "Appointment booked successfully!" : "Appointment updated successfully!";
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            AppointmentBookingForm bookingForm = new AppointmentBookingForm(-1, parentSystem);
            bookingForm.setVisible(true);
        });
    }
}