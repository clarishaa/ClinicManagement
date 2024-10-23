package ClinicManagement;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Main extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main frame = new Main();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Main() {
        // Frame setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1169, 806);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        setLocationRelativeTo(null);

        // Top Panel (Logo)
        JPanel topPanel = new JPanel();
        topPanel.setForeground(new Color(255, 255, 255));
        topPanel.setBackground(new Color(0, 64, 128));  // Dark blue background
        contentPane.add(topPanel, BorderLayout.NORTH);

        JLabel logoLabel = new JLabel("Dental Clinic Management");
        logoLabel.setForeground(new Color(255, 255, 255));
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(logoLabel);

        // Main Dashboard Panel
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new GridLayout(2, 3, 20, 20)); // 2 rows, 3 columns with padding
        dashboardPanel.setBorder(new EmptyBorder(30, 30, 30, 30)); // Padding around the buttons
        contentPane.add(dashboardPanel, BorderLayout.CENTER);

        // Minimalist buttons for the main features
        JButton btnAppointments = new JButton("");
        btnAppointments.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAppointments.setPreferredSize(new Dimension(500, 500));
        btnAppointments.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAppointments.setIcon(new ImageIcon(Main.class.getResource("/ClinicManagement/icons (1).png")));
        btnAppointments.setToolTipText("Manage Appointments");
        btnAppointments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create an instance of AppointmentManagementSystem
                AppointmentManagementSystem appointmentSystem = new AppointmentManagementSystem();
                appointmentSystem.setVisible(true); // Show the Appointment Management System
            }
        });

        JButton btnPatients = new JButton("");
        btnPatients.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPatients.setForeground(SystemColor.window);
        btnPatients.setToolTipText("Manage Patients");
        btnPatients.setHorizontalTextPosition(SwingConstants.CENTER);
        btnPatients.setIcon(new ImageIcon(Main.class.getResource("/ClinicManagement/icons.png")));
        btnPatients.setPreferredSize(new Dimension(500, 500));
        btnPatients.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Patients patient = new Patients();
                patient.setVisible(true); // Show the Patients Management System
            }
        });

        JButton btnTreatments = new JButton("");
        btnTreatments.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTreatments.setHorizontalTextPosition(SwingConstants.CENTER);
        btnTreatments.setIcon(new ImageIcon(Main.class.getResource("/ClinicManagement/icons (2).png")));
        btnTreatments.setPreferredSize(new Dimension(500, 500));
        btnTreatments.setToolTipText("Manage Treatments");
        btnTreatments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Treatments treatment = new Treatments();
                treatment.setVisible(true); // Show the Treatments Management System
            }
        });

        JButton btnInventory = new JButton("");
        btnInventory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnInventory.setIcon(new ImageIcon(Main.class.getResource("/ClinicManagement/icons (4).png")));
        btnInventory.setToolTipText("Manage Inventory");
        btnInventory.setPreferredSize(new Dimension(500, 500));
        btnInventory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Inventory inventory = new Inventory();
                inventory.setVisible(true); // Show the Inventory Management System
            }
        });

        JButton btnBilling = new JButton("");
        btnBilling.setIcon(new ImageIcon(Main.class.getResource("/ClinicManagement/icons (5).png")));
        btnBilling.setToolTipText("Manage Billing");
        btnBilling.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBilling.setPreferredSize(new Dimension(500, 500));

        JButton btnReports = new JButton("");
        btnReports.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnReports.setToolTipText("View Reports");
        btnReports.setPreferredSize(new Dimension(500, 500));

        // Style the buttons (Minimalist look)
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);
        Color buttonColor = new Color(240, 240, 240); // Light gray button background

        btnAppointments.setFont(buttonFont);
        btnAppointments.setBackground(buttonColor);
        btnPatients.setFont(new Font("Century Gothic", Font.BOLD, 45));
        btnPatients.setBackground(buttonColor);
        btnTreatments.setFont(buttonFont);
        btnTreatments.setBackground(buttonColor);
        btnInventory.setFont(buttonFont);
        btnInventory.setBackground(buttonColor);
        btnBilling.setFont(buttonFont);
        btnBilling.setBackground(buttonColor);
        btnReports.setFont(buttonFont);
        btnReports.setBackground(buttonColor);

        // Add buttons to the dashboard
        dashboardPanel.add(btnAppointments);
        dashboardPanel.add(btnPatients);
        dashboardPanel.add(btnTreatments);
        dashboardPanel.add(btnInventory);
        dashboardPanel.add(btnBilling);
        dashboardPanel.add(btnReports);
    }

  
}
