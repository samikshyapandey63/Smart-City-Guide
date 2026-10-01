import javax.swing.*;
import java.awt.*;

public class AdminLogin extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;

    public AdminLogin() {

        setTitle("Smart City Guide - Admin Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel title = new JLabel("ADMIN LOGIN", SwingConstants.CENTER);
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        loginButton = new JButton("Login");

        panel.add(title);
        panel.add(new JLabel(""));

        panel.add(usernameLabel);
        panel.add(usernameField);

        panel.add(passwordLabel);
        panel.add(passwordField);

        panel.add(new JLabel(""));
        panel.add(loginButton);

        add(panel);

        // Login button action
        loginButton.addActionListener(e -> login());
    }

    private void login() {

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Predefined admin credentials
        if (username.equals("admin") && password.equals("admin123")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Login Successful!"
            );

            new AdminDashboard();
            dispose();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password!",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        new AdminLogin().setVisible(true);
    }
}
