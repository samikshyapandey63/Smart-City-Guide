import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {

        setTitle("Smart City Guide - Admin Dashboard");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ================= MAIN PANEL =================

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);


        // ================= TOP HEADER =================

        JPanel headerPanel =
                new JPanel(new BorderLayout());

        headerPanel.setBackground(new Color(45, 55, 72));
        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 25, 15, 20
                )
        );


        JLabel title =
                new JLabel("SMART CITY GUIDE");

        title.setForeground(Color.WHITE);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );


        JButton logoutButton =
                new JButton("LOGOUT");

        logoutButton.setPreferredSize(
                new Dimension(100, 35)
        );


        headerPanel.add(
                title,
                BorderLayout.WEST
        );

        headerPanel.add(
                logoutButton,
                BorderLayout.EAST
        );


        // ================= CENTER =================

        JPanel centerPanel =
                new JPanel(new BorderLayout());

        centerPanel.setBackground(Color.WHITE);

        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 60, 40, 60
                )
        );


        JLabel dashboardTitle =
                new JLabel(
                        "ADMIN DASHBOARD",
                        SwingConstants.CENTER
                );

        dashboardTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        26
                )
        );


        centerPanel.add(
                dashboardTitle,
                BorderLayout.NORTH
        );


        // ================= CARD PANEL =================

        JPanel cardPanel =
                new JPanel(
                        new GridLayout(
                                3,
                                2,
                                25,
                                25
                        )
                );

        cardPanel.setBackground(Color.WHITE);

        cardPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 20, 10, 20
                )
        );


        // ================= BUTTONS =================

        JButton dashboardButton =
                createCardButton("DASHBOARD");

        JButton placesButton =
                createCardButton("MANAGE PLACES");

        JButton activitiesButton =
                createCardButton("ACTIVITIES");

        JButton suggestionsButton =
                createCardButton("SUGGESTIONS");

        JButton reviewsButton =
                createCardButton("REVIEWS");


        // ================= ADD BUTTONS =================

        cardPanel.add(dashboardButton);

        cardPanel.add(placesButton);

        cardPanel.add(activitiesButton);

        cardPanel.add(suggestionsButton);

        cardPanel.add(reviewsButton);


        // Empty sixth space
        cardPanel.add(new JPanel());


        centerPanel.add(
                cardPanel,
                BorderLayout.CENTER
        );


        // ================= BUTTON ACTIONS =================

        dashboardButton.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                    this,
                    "You are already on the dashboard."
            );

        });


        placesButton.addActionListener(e -> {

            new ManagePlaces();

        });


        activitiesButton.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                    this,
                    "Activities section coming soon."
            );

        });


        suggestionsButton.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                    this,
                    "Suggestions section coming soon."
            );

        });


        reviewsButton.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                    this,
                    "Reviews section coming soon."
            );

        });


        logoutButton.addActionListener(e -> {

            int choice =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to logout?",
                            "Logout",
                            JOptionPane.YES_NO_OPTION
                    );


            if (choice ==
                    JOptionPane.YES_OPTION) {

                dispose();

                new AdminLogin();

            }

        });


        // ================= ADD TO FRAME =================

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );


        add(mainPanel);

        setVisible(true);
    }


    // ================= CARD BUTTON STYLE =================

    private JButton createCardButton(
            String text) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        button.setFocusPainted(false);

        button.setBackground(
                new Color(235, 240, 245)
        );

        button.setBorder(
                BorderFactory.createLineBorder(
                        new Color(200, 205, 210),
                        1
                )
        );

        return button;
    }


    // ================= MAIN =================

    public static void main(String[] args) {

        new AdminDashboard();

    }
}
