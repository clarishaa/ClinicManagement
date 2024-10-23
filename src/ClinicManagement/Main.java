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
        setBounds(100, 100, 720, 547);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // Top Panel (Logo)
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(220, 220, 220));  // Light gray background
        contentPane.add(topPanel, BorderLayout.NORTH);

        JLabel logoLabel = new JLabel("Dental Clinic Management");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(logoLabel);

        // Main Dashboard Panel
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new GridLayout(2, 3, 20, 20)); // 2 rows, 3 columns with padding
        dashboardPanel.setBorder(new EmptyBorder(30, 30, 30, 30)); // Padding around the buttons
        contentPane.add(dashboardPanel, BorderLayout.CENTER);

        // Minimalist buttons for the main features
        JButton btnAppointments = new JButton("Appointments");
        btnAppointments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create an instance of AppointmentManagementSystem
                AppointmentManagementSystem appointmentSystem = new AppointmentManagementSystem();
                // Optionally, set the frame or panel where this should be displayed
                appointmentSystem.setVisible(true); // Assuming you have a method to show the GUI
            }
        });

        JButton btnPatients = new JButton("Patients");
        btnPatients.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Patients patient = new Patients();
        		patient .setVisible(true);
        	}
        });
        JButton btnTreatments = new JButton("Treatments");
        JButton btnBilling = new JButton("Billing");
        JButton btnReports = new JButton("Reports");
        JButton btnSettings = new JButton("Settings");

        // Style the buttons (Minimalist look)
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);
        Color buttonColor = new Color(240, 240, 240); // Light gray button background

        btnAppointments.setFont(buttonFont);
        btnAppointments.setBackground(buttonColor);
        btnPatients.setFont(buttonFont);
        btnPatients.setBackground(buttonColor);
        btnTreatments.setFont(buttonFont);
        btnTreatments.setBackground(buttonColor);
        btnBilling.setFont(buttonFont);
        btnBilling.setBackground(buttonColor);
        btnReports.setFont(buttonFont);
        btnReports.setBackground(buttonColor);
        btnSettings.setFont(buttonFont);
        btnSettings.setBackground(buttonColor);

        // Add buttons to the dashboard
        dashboardPanel.add(btnAppointments);
        dashboardPanel.add(btnPatients);
        dashboardPanel.add(btnTreatments);
        dashboardPanel.add(btnBilling);
        dashboardPanel.add(btnReports);
        dashboardPanel.add(btnSettings);
    }
}
