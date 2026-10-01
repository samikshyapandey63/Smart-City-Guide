import javax.swing.*;
import java.awt.*;

public class MainScreen {
    public static void main(String[] args) {

        JFrame frame = new JFrame();

        frame.setSize(800, 800);
        frame.setLayout(null);

        frame.getContentPane().setBackground(new Color(190, 215, 235));

        // TITLE

        JLabel title = new JLabel("SMART CITY GUIDE");

        title.setFont(new Font("Times New Roman", Font.BOLD, 40));
        title.setForeground(Color.BLACK);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        title.setBounds(0, 60, 800, 60);

        frame.add(title);


        // EXPLORE

        JLabel l1 = new JLabel("Explore • Discover • Thrive");

        l1.setFont(new Font("Segoe Script", Font.ITALIC, 20));
        l1.setForeground(Color.WHITE);
        l1.setHorizontalAlignment(SwingConstants.CENTER);

        l1.setBounds(0, 130, 800, 40);

        frame.add(l1);


        // LOGIN BUTTON

        JButton b = new JButton("Login");

        b.setBounds(360, 210, 80, 40);

        b.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        b.setForeground(Color.WHITE);
        b.setBackground(Color.GRAY);

        b.addActionListener(e -> {
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });

        frame.add(b);


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}