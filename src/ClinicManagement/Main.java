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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1169, 806);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource("/ClinicManagement/icons (6).png")).getImage());

        JPanel topPanel = new JPanel();
        topPanel.setForeground(new Color(255, 255, 255));
        topPanel.setBackground(new Color(0, 64, 128));
        contentPane.add(topPanel, BorderLayout.NORTH);

        JLabel logoLabel = new JLabel("Dental Clinic Management");
        logoLabel.setForeground(new Color(255, 255, 255));
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(logoLabel);

        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new GridLayout(2, 3, 20, 20));
        dashboardPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        contentPane.add(dashboardPanel, BorderLayout.CENTER);

        JButton btnAppointments = new JButton("");
        btnAppointments.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAppointments.setPreferredSize(new Dimension(500, 500));
        btnAppointments.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAppointments.setIcon(new ImageIcon(Main.class.getResource("/ClinicManagement/icons (1).png")));
        btnAppointments.setToolTipText("Manage Appointments");
        btnAppointments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AppointmentManagementSystem appointmentSystem = new AppointmentManagementSystem();
                appointmentSystem.setVisible(true);
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
                patient.setVisible(true);
            }
        });

        JButton btnTreatments = new JButton("");
        btnTreatments.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTreatments.setHorizontalTextPosition(SwingConstants.CENTER);
        btnTreatments.setIcon(new ImageIcon(Main.class.getResource("/ClinicManagement/6.png")));
        btnTreatments.setPreferredSize(new Dimension(500, 500));
        btnTreatments.setToolTipText("Manage Treatments");
        btnTreatments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Treatments treatment = new Treatments();
                treatment.setVisible(true);
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
                inventory.setVisible(true);
            }
        });

        JButton btnBilling = new JButton("");
        btnBilling.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Billing billing = new Billing();
                billing.setVisible(true);
        	}
        });
        btnBilling.setIcon(new ImageIcon(Main.class.getResource("/ClinicManagement/icons (5).png")));
        btnBilling.setToolTipText("Manage Billing");
        btnBilling.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBilling.setPreferredSize(new Dimension(500, 500));

        JButton btnLogout = new JButton("");
        btnLogout.setIcon(new ImageIcon(Main.class.getResource("/ClinicManagement/9.png")));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setToolTipText("Logout");
        btnLogout.setPreferredSize(new Dimension(500, 500));

        Font buttonFont = new Font("Arial", Font.PLAIN, 18);
        Color buttonColor = new Color(240, 240, 240);

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
        btnLogout.setFont(buttonFont);
        btnLogout.setBackground(buttonColor);

        dashboardPanel.add(btnAppointments);
        dashboardPanel.add(btnPatients);
        dashboardPanel.add(btnTreatments);
        dashboardPanel.add(btnInventory);
        dashboardPanel.add(btnBilling);
        dashboardPanel.add(btnLogout);
    }

  
}
