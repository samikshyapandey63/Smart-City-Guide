import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
//import dao.UserDAO;
//import exception.InvalidLoginException;
//import model.User;
//import ui.DashboardFrame;

public class MainScreen {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Smart City Guide");
        frame.setSize(800, 600);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(190, 215, 235));

        // TITLE

        JLabel title = new JLabel("SMART CITY GUIDE");

        title.setFont(new Font("Times New Roman", Font.BOLD, 40));
        title.setForeground(Color.BLACK);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(0, 50, 800, 60);
        frame.add(title);


        // EXPLORE

        JLabel l1 = new JLabel("Explore • Discover • Thrive");
        l1.setFont(new Font("Segoe Script", Font.ITALIC, 20));
        l1.setForeground(Color.WHITE);
        l1.setHorizontalAlignment(SwingConstants.CENTER);
        l1.setBounds(0, 110, 800, 40);
        frame.add(l1);

        //Username
        JLabel l3 = new JLabel("Username");
        l3.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        l3.setForeground(Color.WHITE);
        l3.setBounds(250, 185, 300, 24);
        frame.add(l3);

        JTextField userField = new JTextField();
        userField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        userField.setBounds(250, 212, 300, 36);
        frame.add(userField);

        //Password
        JLabel l4 = new JLabel("Password");
        l4.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        l4.setForeground(Color.WHITE);
        l4.setBounds(250, 260, 300, 24);
        frame.add(l4);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        passwordField.setBounds(250, 287, 300, 36);
        frame.add(passwordField);

        //tick
        char hiddenEcho = passwordField.getEchoChar();
        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        showPass.setForeground(Color.WHITE);
        showPass.setOpaque(false);                      // keeps the blue background behind it
        showPass.setBounds(246, 328, 200, 28);
        showPass.addActionListener(e ->
                passwordField.setEchoChar(showPass.isSelected() ? (char) 0 : hiddenEcho));
        frame.add(showPass);

        //error message
        JLabel errorLabel = new JLabel("");
        errorLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        errorLabel.setForeground(new Color(180, 0, 0));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setBounds(200, 358, 400, 22);
        frame.add(errorLabel);

        // LOGIN BUTTON
        JButton b = new JButton("Login");
        b.setBounds(250, 375, 300, 42);
        b.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(60, 90, 130));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);

        b.addActionListener(e -> {
            errorLabel.setText("");
            String username = userField.getText().trim();
            String password = new String(passwordField.getPassword());

        //validation ko area
            if (username.equals("")) {
                errorLabel.setText("Enter your username.");
                userField.requestFocus();
                return;
            }
            try {
                UserDAO dao = new UserDAO();

                // Check if this username is already locked
                long lockedSeconds = dao.getRemainingLockSeconds(username);

                if (lockedSeconds > 0) {

                    b.setEnabled(false);
                    userField.setEnabled(false);
                    passwordField.setEnabled(false);
                    showPass.setEnabled(false);

                    errorLabel.setText(
                            "Too many wrong attempts. Please wait "
                                    + lockedSeconds + " seconds."
                    );

                    Timer timer = new Timer(1000, null);

                    final long[] remaining = {lockedSeconds};

                    timer.addActionListener(event -> {

                        remaining[0]--;

                        if (remaining[0] > 0) {

                            errorLabel.setText(
                                    "Too many wrong attempts. Please wait "
                                            + remaining[0] + " seconds."
                            );

                        } else {

                            timer.stop();

                            b.setEnabled(true);
                            userField.setEnabled(true);
                            passwordField.setEnabled(true);
                            showPass.setEnabled(true);

                            errorLabel.setText("You can try again.");
                            passwordField.requestFocus();
                        }
                    });

                    timer.start();

                    return;
                }
            if (password.equals("")) {
                errorLabel.setText("Enter your password.");
                passwordField.requestFocus();
                return;
            }

                User user = dao.login(username, password);

                DashboardFrame dashboard = new DashboardFrame(user);
                dashboard.setVisible(true);
                frame.dispose();

            } catch (InvalidLoginException ex) {

                passwordField.setText("");

                String message = ex.getMessage();

                if (message.equals("USERNAME_NOT_FOUND")) {

                    errorLabel.setText("Username does not exist.");
                    userField.requestFocus();

                } else if (message.startsWith("WRONG_PASSWORD:")) {

                    String attemptsLeft = message.substring(
                            "WRONG_PASSWORD:".length()
                    );

                    errorLabel.setText(
                            "Wrong password. Attempts left: " + attemptsLeft
                    );

                    passwordField.requestFocus();

                } else if (message.startsWith("LOCKED:")) {

                    String secondsText = message.substring(
                            "LOCKED:".length()
                    );

                    int secondsLeft = Integer.parseInt(secondsText);

                    b.setEnabled(false);
                    userField.setEnabled(false);
                    passwordField.setEnabled(false);
                    showPass.setEnabled(false);

                    errorLabel.setText(
                            "Too many wrong attempts. Please wait "
                                    + secondsLeft + " seconds."
                    );

                    Timer timer = new Timer(1000, null);

                    final int[] remaining = {secondsLeft};

                    timer.addActionListener(event -> {

                        remaining[0]--;

                        if (remaining[0] > 0) {

                            errorLabel.setText(
                                    "Too many wrong attempts. Please wait "
                                            + remaining[0] + " seconds."
                            );

                        } else {

                            timer.stop();

                            b.setEnabled(true);
                            userField.setEnabled(true);
                            passwordField.setEnabled(true);
                            showPass.setEnabled(true);

                            errorLabel.setText("You can try again.");
                            passwordField.requestFocus();
                        }
                    });

                    timer.start();
                }

            } catch (SQLException ex) {

                errorLabel.setText(
                        "Could not reach the database. Please try again later?"
                );

                ex.printStackTrace();
            }
        });
        frame.add(b);
        frame.getRootPane().setDefaultButton(b);

        //sign up
        JLabel l2 = new JLabel("Dont have an account?");
        l2.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        l2.setForeground(Color.WHITE);
        l2.setHorizontalAlignment(SwingConstants.CENTER);
        l2.setBounds(0, 438, 800, 30);
        frame.add(l2);

        JButton b1 = new JButton("Sign Up");
        b1.setBounds(330, 472, 140, 40);
        b1.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        b1.setForeground(Color.WHITE);
        b1.setBackground(new Color(60, 90, 130));
        b1.setOpaque(true);
        b1.setBorderPainted(false);
        b1.setFocusPainted(false);

        b1.addActionListener(e -> {
            SignupScreen Signup= new SignupScreen(frame);
            Signup.setVisible(true);
            frame.dispose();
        });
        frame.add(b1);

        frame.setVisible(true);
    }
}