package ClinicManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;
    private Connection connection;

    public LoginForm() {
        setTitle("CleanSmile Dental Clinic - Login");
        setSize(792, 576); // Set size to 11x8 inches in pixels
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 792, 576, 20, 20));
        setBackground(new Color(0, 0, 0, 0));

        // Initialize database connection
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Main panel
        JPanel mainPanel = new JPanel() {
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
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo
        ImageIcon logoIcon = new ImageIcon("C:\\Users\\adley\\OneDrive\\Pictures\\Screenshots\\LogoClinic.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(logoLabel, gbc);

        // Email Field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 153, 153), 2, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridy = 2;
        mainPanel.add(emailField, gbc);

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 153, 153), 2, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridy = 4;
        mainPanel.add(passwordField, gbc);

        // Login Button
        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(0, 153, 153));
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 20, 10, 20);
        mainPanel.add(loginButton, gbc);

        // Create Account Button
        createAccountButton = new JButton("Create Account");
        styleButton(createAccountButton, new Color(70, 130, 180));
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 20, 20, 20);
        mainPanel.add(createAccountButton, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(0, 153, 153));
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> System.exit(0));
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.setOpaque(false);
        closePanel.add(closeButton);
        add(closePanel, BorderLayout.NORTH);

        // Action Listeners
        loginButton.addActionListener(this::performLogin);
        createAccountButton.addActionListener(e -> openCreateAccountForm());

        // Make the window draggable
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

    private void styleButton(JButton button, Color backgroundColor) {
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
    }

    private void performLogin(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        int userType = authenticateUser(email, password);
        if (userType != -1) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Welcome", JOptionPane.INFORMATION_MESSAGE);
            if (userType == 1) {
                openAppointmentManagementSystem();
            } else {
                openAppointmentBookingForm();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int authenticateUser(String email, String password) {
        // Check for admin credentials
        if (email.equals("admin@gmail.com") && password.equals("admin")) {
            return 1; // Admin user
        }

        // Check for regular user credentials
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password); // Note: In a real application, passwords should be hashed
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return 0; // Regular user
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1; // Authentication failed
    }

    private void openCreateAccountForm() {
        SwingUtilities.invokeLater(() -> {
            CreateAccountForm createAccountForm = new CreateAccountForm();
            createAccountForm.setVisible(true);
            this.dispose(); // Close the login form
        });
    }
    
    private void openAppointmentManagementSystem() {
        SwingUtilities.invokeLater(() -> {
            AppointmentManagementSystem ams = new AppointmentManagementSystem();
            ams.setVisible(true);
            this.dispose(); // Close the login form
        });
    }
    
    private void openAppointmentBookingForm() {
        SwingUtilities.invokeLater(() -> {
            AppointmentBookingForm abf = new AppointmentBookingForm(-1, null);
            abf.setVisible(true);
            this.dispose(); // Close the login form
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
