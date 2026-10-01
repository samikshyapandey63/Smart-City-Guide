import javax.swing.*;
import java.lang.classfile.Label;

public class LoginScreen extends JFrame {

    public LoginScreen() {

        setTitle("Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Login
        JTextArea loginArea = new JTextArea();
        loginArea.setBounds(150, 80, 200, 40);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 150, 200, 40);

        JLabel loginLabel = new JLabel("Login:");
        loginLabel.setBounds(80, 80, 60, 40);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(70, 150, 80, 40);

        // Add components
        add(loginLabel);
        add(loginArea);
        add(passwordLabel);
        add(passwordField);

        setLayout(null);
        setVisible(true);

    }
}
