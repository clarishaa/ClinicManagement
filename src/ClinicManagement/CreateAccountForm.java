package ClinicManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

public class CreateAccountForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton createButton;
    private JButton loginButton;

    public CreateAccountForm() {
        setTitle("Create Account - CleanSmile Dental Clinic");
        setSize(792, 576); // Set the size to 11x8 inches in pixels
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 792, 576, 20, 20));
        setBackground(new Color(0, 0, 0, 0));
        setLocationRelativeTo(null);
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

        // Name Label and Field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(nameLabel, gbc);

        nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 153, 153), 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        // Email Label and Field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField(15);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 153, 153), 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 153, 153), 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Create Account Button
        createButton = new JButton("Create Account");
        styleButton(createButton, new Color(0, 153, 153));
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(createButton, gbc);

        // Go to Login Button
        loginButton = new JButton("Go to Login");
        styleButton(loginButton, new Color(70, 130, 180));
        gbc.gridx = 1;
        gbc.gridy = 5;
        mainPanel.add(loginButton, gbc);

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
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                try {
					AddUser.addUser(name, email, password);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}  // Call to the AddUser class
                JOptionPane.showMessageDialog(null, "Account created successfully!");
                new LoginForm().setVisible(true);
                dispose();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });

        // Make the window draggable
        makeDraggable();
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
    }

    private void makeDraggable() {
        MouseAdapter ma = new MouseAdapter() {
            private int pressedX;
            private int pressedY;

            public void mousePressed(java.awt.event.MouseEvent e) {
                pressedX = e.getX();
                pressedY = e.getY();
            }

            public void mouseDragged(java.awt.event.MouseEvent e) {
                setLocation(getX() + e.getX() - pressedX, getY() + e.getY() - pressedY);
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CreateAccountForm createAccountForm = new CreateAccountForm();
            createAccountForm.setVisible(true);
        });
    }
}
