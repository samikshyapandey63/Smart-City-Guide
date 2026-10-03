import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class SignupScreen extends JFrame implements ActionListener {

    JLabel title, hint, l1, l2, l3, l4, errorLabel;
    JTextField nameField, userField;
    JPasswordField passField, confirmField;
    JCheckBox showPass;
    JButton createBtn, backBtn;
    JFrame loginFrame;

    SignupScreen(JFrame loginFrame) {
        this.loginFrame= loginFrame;
        setTitle("Smart City Guide - Sign Up");
        setSize(800, 600);
        setLayout(null);
        getContentPane().setBackground(new Color(190, 215, 235));

        // TITLE + RULES
        title = new JLabel("CREATE ACCOUNT");
        title.setFont(new Font("Times New Roman", Font.BOLD, 34));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(0, 25, 800, 50);
        add(title);

        hint = new JLabel("Username: 4-20 letters, digits or _     Password: 6+ characters with a letter and a number");
        hint.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        hint.setForeground(Color.DARK_GRAY);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        hint.setBounds(0, 78, 800, 24);
        add(hint);

        // FULL NAME
        l1 = new JLabel("Full name");
        l1.setBounds(250, 115, 300, 22);
        add(l1);
        nameField = new JTextField();
        nameField.setBounds(250, 138, 300, 34);
        add(nameField);

        // USERNAME
        l2 = new JLabel("Username");
        l2.setBounds(250, 180, 300, 22);
        add(l2);
        userField = new JTextField();
        userField.setBounds(250, 203, 300, 34);
        add(userField);

        // PASSWORD
        l3 = new JLabel("Password");
        l3.setBounds(250, 245, 300, 22);
        add(l3);
        passField = new JPasswordField();
        passField.setEchoChar('*');
        passField.setBounds(250, 268, 300, 34);
        add(passField);

        // CONFIRM PASSWORD
        l4 = new JLabel("Confirm password");
        l4.setBounds(250, 310, 300, 22);
        add(l4);
        confirmField = new JPasswordField();
        confirmField.setEchoChar('*');
        confirmField.setBounds(250, 333, 300, 34);
        add(confirmField);

        // SHOW PASSWORD (the tick mark)
        showPass = new JCheckBox("Show passwords");
        showPass.setBackground(new Color(190, 215, 235));
        showPass.setBounds(246, 372, 200, 26);
        add(showPass);

        // ERROR MESSAGE
        errorLabel = new JLabel("");
        errorLabel.setForeground(new Color(180, 0, 0));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setBounds(200, 402, 400, 24);
        add(errorLabel);

        // BUTTONS
        createBtn = new JButton("Create account");
        createBtn.setBounds(250, 432, 300, 42);
        createBtn.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        createBtn.setForeground(Color.WHITE);
        createBtn.setBackground(Color.GRAY);
        add(createBtn);

        backBtn = new JButton("Already have an account? Back to login");
        backBtn.setBounds(250, 486, 300, 35);
        backBtn.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        add(backBtn);

        showPass.addActionListener(this);
        createBtn.addActionListener(this);
        backBtn.addActionListener(this);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == showPass) {
            if (showPass.isSelected()) {
                passField.setEchoChar((char) 0);
                confirmField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('*');
                confirmField.setEchoChar('*');
            }
        }

        if (e.getSource() == createBtn) {
            register();
        }

        if (e.getSource() == backBtn) {
            loginFrame.setVisible(true);
            dispose();
        }
    }

    void showError(String message, JComponent field) {
        errorLabel.setText(message);
        if (field != null) {
            field.requestFocus();
        }
    }

    void register() {
        errorLabel.setText("");
        String name = nameField.getText().trim();
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());
        String confirm = new String(confirmField.getPassword());

        // ----- validation, one field at a time (Validator returns null when the value is fine) -----
        String error = Validator.checkName(name);
        if (error != null) {
            showError(error, nameField);
            return;
        }

        error = Validator.checkUsername(username);
        if (error != null) {
            showError(error, userField);
            return;
        }

        error = Validator.checkPassword(password, username);
        if (error != null) {
            showError(error, passField);
            return;
        }

        if (confirm.equals("")) {
            showError("Confirm your password.", confirmField);
            return;
        }
        if (!password.equals(confirm)) {
            confirmField.setText("");
            showError("Passwords do not match.", confirmField);
            return;
        }

        // ----- database -----
        try {
            UserDAO dao = new UserDAO();

            if (dao.usernameExists(username)) {
                showError("That username is already taken.", userField);
                return;
            }

            dao.registerTourist(name, username, password);     // role is always TOURIST

            JOptionPane.showMessageDialog(this, "Account created! You can log in now.");
            loginFrame.setVisible(true);
            dispose();
        } catch (SQLException ex) {
            showError("Could not save the account. Is MySQL running?", null);
        }
    }


}
